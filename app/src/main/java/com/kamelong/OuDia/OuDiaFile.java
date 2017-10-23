package com.kamelong.OuDia;


import java.io.*;
import java.util.ArrayList;

import com.kamelong.aodia.diadata.Operation;
import com.kamelong.tool.Color;
import com.kamelong.tool.Font;
import com.kamelong.tool.ShiftJISBufferedReader;

/**
 * OuDia形式の1ファイルを取り扱う.
 * OuDiaSecondのファイルにも対応させる.
 * 内部に複数のダイヤを格納することができるが、駅リスト、種別リストは一つしか持てない.
 *
 * @author  KameLong
 */
public class OuDiaFile {
    /**
     *路線名.
     */
    protected String lineName="";
    /**
     * ダイヤ名。
     * 複数のダイヤを保持することができるためArrayListを用いる。
     */
    protected ArrayList<String> diaName=new ArrayList<>();
    /**
     * 駅。
     * 複数の駅を保持することができるためArrayListを用いる。
     */
    protected ArrayList<OuDiaStation> station=new ArrayList<>();
    /**
     * 種別.
     * 複数の種別を保持することができるためArrayListを用いる。
     */
    protected ArrayList<OuDiaTrainType> trainType=new ArrayList<>();
    /**
     * Trainは1本の列車に相当する
     * 最初のArrayListはダイヤの数に相当する
     * ArrayListの中に配列があるが、これは上りと下りの２つ(確定)の時刻表があるため、配列を用いている
     * 配列の内部に再びArrayListがあるが、これは各時刻表中の列車の本数分の大きさを持つ
     */
    protected ArrayList<ArrayList<? extends OuDiaTrain>[]> train=new ArrayList<>();
    /**
     * コメント。
     * oudiaデータには路線ごとにコメントがついている。
     * ダイヤごとにコメントをつけたい場合はArrayListに拡張しないといけない。
     */
    protected String comment="";
    /**
     * ダイヤグラム起点時刻。
     */
    protected int diagramStartTime=10800;

    /**
     * DiagramDgrYZahyouKyoriDefault
     */
    protected int zahyouKyoriDefault=60;
    /**
     * 時刻表フォント一覧
     */
    protected ArrayList<Font> jikokuhyouFont =new ArrayList<>();
    /**
     * JikokuhyouVFont
     */
    protected Font jikokuVFont =Font.OUDIA_DEFAULT;
    /**
     * DiaEkimeiFont
     */
    protected Font diaEkimeiFont=Font.OUDIA_DEFAULT;
    /**
     * DiaJikokuFont
     */
    protected Font diaJikokuFont=Font.OUDIA_DEFAULT;
    /**
     * DiaRessyaFont
     */
    protected Font diaRessyaFont=Font.OUDIA_DEFAULT;
    /**
     * CommentFont
     */
    protected Font commentFont=Font.OUDIA_DEFAULT;
    /**
     * DiaMojiColor
     */
    protected Color diaMojiColor=new Color();
    /**
     * DiaHaikeiColor
     */
    protected Color diaHaikeiColor= new Color();
    /**
     * DiaRessyaColor
     */
    protected Color diaResyaColor=new Color();
    /**
     * DiaJikuColor
     */
    protected Color diaJikuColor=new Color();
    /**
     * EkimeiLength
     * -1はデフォルト
     * default=6
     */
    protected int stationNameLength=-1;
    /**
     * JikokuhyouRessyaWidth
     * -1はデフォルト
     * default=5
     *
     */
    protected int trainWidth =6;
    /**
     * AnySecondIncDec1
     */
    protected int anySecondIncDec1=5;
    /**
     * AnySecondIncDec2
     */
    protected int anySecondIncDec2=15;
    /**
     * FileType
     */
    protected String fileType="";


    /**
     * コンストラクタ。
     * @param file 開きたいファイル
     *
     * コンストラクタでは読み込みファイルが与えられるので、そのファイルを読み込む。
     * 読み込む処理はloadDiaに書かれているので適宜呼び出す。
     * oudファイルはShiftJisで書かれているので考慮する必要がある。
     *
     * fileがnullの時はnullPointerExceptionが発生する
     *
     */
    public OuDiaFile(File file){
        try {
            FileInputStream is = new FileInputStream(file);
            if(file.getPath().endsWith(".oud")||file.getPath().endsWith(".oud2")){
                InputStreamReader filereader = new InputStreamReader(is, "Shift_JIS");
                BufferedReader br = new ShiftJISBufferedReader(filereader);
                loadDia(br);
            }

        } catch (Exception e) {
            //ファイル読み込み失敗
            e.printStackTrace();
        }
    }
    public OuDiaFile(){

    }
    /**
     * OuDiaTrainを生成する
     * これをオーバーライドすることで任意のOuDiaTrainを継承したTrainクラスで生成できる
     */
    protected OuDiaTrain newTrain(){
        return new OuDiaTrain(this);
    }
    /**
     * OuDiaTrainTypeを生成する
     * これをオーバーライドすることで任意のOuDiaTrainTypeを継承したTrainクラスで生成できる
     */
    protected OuDiaTrainType newTrainType(){
        return new OuDiaTrainType();
    }
    /**
     * OuDiaStationを生成する
     * これをオーバーライドすることで任意のOuDiaStationを継承したStationクラスで生成できる
     */
    protected OuDiaStation newStation(){
        return new OuDiaStation();
    }
    /**
     * OuDiaStationを生成する
     * これをオーバーライドすることで任意のOuDiaStationを継承したStationクラスで生成できる
     */
    protected Font newFont(){
        return new Font();
    }
    protected String filePath="";


    /**
     * oudファイルを読み込んでオブジェクトを構成する。
     * @param br  BufferReader of .oud fille.  forbidden @null
     */
    protected void loadDia(BufferedReader br){
        try{
            String line="";
            while((line=br.readLine())!=null) {
                if(line.equals("Dia.")){
                    line=br.readLine();
                    diaName.add(line.split("=",-1)[1]);
                    ArrayList<OuDiaTrain>[] trainArray=new ArrayList[2];
                    trainArray[0]=new ArrayList<>();
                    trainArray[1]=new ArrayList<>();
                    while(!line.equals(".")){
                        if(line.equals("Ressya.")){
                            int direct=0;
                            OuDiaTrain t=newTrain();
                            while(!line.equals(".")){

                                if(line.split("=",-1)[0].equals("Houkou")){
                                    if(line.split("=",-1)[1].equals("Kudari")){
                                        direct=0;
                                    }
                                    if(line.split("=",-1)[1].equals("Nobori")){
                                        direct=1;
                                    }
                                    t.setDirect(direct);
                                }

                                if(line.split("=",-1)[0].equals("Syubetsu")) {
                                    t.setType(Integer.parseInt(line.split("=",-1)[1]));
                                }
                                if(line.split("=",-1)[0].equals("Ressyamei")) {
                                    t.setName(line.split("=",-1)[1]);
                                }
                                if(line.split("=",-1)[0].equals("Gousuu")) {
                                    t.setCount(line.split("=",-1)[1]);
                                }
                                if(line.split("=",-1)[0].equals("Ressyabangou")) {
                                    t.setNumber(line.split("=",-1)[1]);
                                }
                                if(line.split("=",-1)[0].equals("Bikou")) {
                                    t.setRemark(line.split("=",-1)[1]);
                                }

                                if(line.split("=",-1)[0].equals("EkiJikoku")) {
                                    try {
                                        t.setTime(line.split("=",-1)[1], direct);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                line=br.readLine();
                            }
                            if(direct!=-1) {
                                trainArray[direct].add(t);
                            }
                        }
                        line=br.readLine();
                        //Diaの終わりは２つの終了行が並んだ時
                        if(line.equals(".")){
                            line=br.readLine();
                        }
                    }
                    trainArray[0].trimToSize();
                    trainArray[1].trimToSize();
                    train.add(trainArray);
                }
                if(line.equals("Ressyasyubetsu.")){
                    OuDiaTrainType mTrainType=newTrainType();
                    while(!line.equals(".")){
                        if(line.split("=",-1)[0].equals("Syubetsumei")){
                            mTrainType.setName(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Ryakusyou")){
                            mTrainType.setShortName(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("JikokuhyouMojiColor")){
                            mTrainType.setTextColor(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("DiagramSenColor")){
                            mTrainType.setDiaColor(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("DiagramSenStyle")){
                            mTrainType.setLineStyle(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("DiagramSenIsBold")){
                            mTrainType.setLineBold(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("StopMarkDrawType")){
                            mTrainType.setShowStop(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("JikokuhyouFontIndex")){
                            mTrainType.fontNumber=Integer.parseInt(line.split("=",-1)[1]);
                        }
                        line=br.readLine();
                    }
                    trainType.add(mTrainType);
                }
                if(line.equals("Eki.")){
                    OuDiaStation mStation=newStation();
                    while(!line.equals(".")){
                        if(line.split("=",-1)[0].equals("Ekimei")){
                            mStation.setName(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Ekijikokukeisiki")){
                            mStation.setStationTimeShow(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Ekikibo")){
                            mStation.setSize(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Kyoukaisen")){
                            mStation.setBorder(Integer.valueOf(line.split("=",-1)[1]));
                        }
                        line=br.readLine();
                    }
                    station.add(mStation);
                }
                if(line.equals("Rosen.")){
                    line=br.readLine();
                    lineName=line.split("=",-1)[1];
                }
                if(line.split("=",-1)[0].equals("Comment")){
                    comment=line.split("=",-1)[1];
                    comment=comment.replace("\\n","\n");
                }
                if(line.split("=",-1)[0].equals("KitenJikoku")){
                            String startTime=(line.split("=",-1)[1]);
                            if(startTime.length()==4){
                                diagramStartTime=Integer.parseInt(startTime.substring(0,2))*3600;
                                diagramStartTime+=Integer.parseInt(startTime.substring(2,4))*60;
                            }
                            if(startTime.length()==3){
                                diagramStartTime=Integer.parseInt(startTime.substring(0,1))*3600;
                                diagramStartTime+=Integer.parseInt(startTime.substring(1,3))*60;

                            }
                }
                if(line.split("=",-1)[0].equals("DiagramDgrYZahyouKyoriDefault")){
                    zahyouKyoriDefault=Integer.parseInt(line.split("=",-1)[1]);
                }
                if(line.split("=",-1)[0].equals("JikokuhyouFont")){
                    Font font=newFont();
                    font.height=Integer.parseInt(line.split("=",-1)[2].split(";",-1)[0]);
                    font.name=line.split("=",-1)[3].split(";",-1)[0];
                    font.bold=line.contains("Bold");
                    font.itaric=line.contains("Itaric");
                    jikokuhyouFont.add(font);
                }
                if(line.split("=",-1)[0].equals("JikokuhyouVFont")){
                    jikokuVFont =newFont();
                    try {
                        jikokuVFont.height = Integer.parseInt(line.split("=", -1)[2].split(";", -1)[0]);
                    }catch(Exception e){
                        e.printStackTrace();}
                    jikokuVFont.name=line.split("=",-1)[3].split(";",-1)[0];

                    jikokuVFont.bold=line.contains("Bold");
                    jikokuVFont.itaric=line.contains("Itaric");
                }
                if(line.split("=",-1)[0].equals("DiaEkimeiFont")){
                    diaEkimeiFont=newFont();
                    diaEkimeiFont.height=Integer.parseInt(line.split("=",-1)[2].split(";",-1)[0]);
                    diaEkimeiFont.name=line.split("=",-1)[3].split(";",-1)[0];
                    diaEkimeiFont.bold=line.contains("Bold");
                    diaEkimeiFont.itaric=line.contains("Itaric");
                }
                if(line.split("=",-1)[0].equals("DiaJikokuFont")){
                    diaJikokuFont=newFont();
                    diaJikokuFont.height=Integer.parseInt(line.split("=",-1)[2].split(";",-1)[0]);
                    diaJikokuFont.name=line.split("=",-1)[3].split(";",-1)[0];
                    diaJikokuFont.bold=line.contains("Bold");
                    diaJikokuFont.itaric=line.contains("Itaric");
                }
                if(line.split("=",-1)[0].equals("DiaRessyaFont")){
                    diaRessyaFont=newFont();
                    diaRessyaFont.height=Integer.parseInt(line.split("=",-1)[2].split(";",-1)[0]);
                    diaRessyaFont.name=line.split("=",-1)[3].split(";",-1)[0];
                    diaRessyaFont.bold=line.contains("Bold");
                    diaRessyaFont.itaric=line.contains("Itaric");
                }
                if(line.split("=",-1)[0].equals("CommentFont")){
                    commentFont=newFont();
                    commentFont.height=Integer.parseInt(line.split("=",-1)[2].split(";",-1)[0]);
                    commentFont.name=line.split("=",-1)[3].split(";",-1)[0];
                    commentFont.bold=line.contains("Bold");
                    commentFont.itaric=line.contains("Itaric");
                }
                if(line.split("=",-1)[0].equals("DiaMojiColor")){
                    diaMojiColor.setOuDiaColor(line.split("=", -1)[1]);
                }
                if(line.split("=",-1)[0].equals("DiaHaikeiColor")){
                    diaHaikeiColor.setOuDiaColor(line.split("=", -1)[1]);
                }
                if(line.split("=",-1)[0].equals("DiaRessyaColor")){
                    diaResyaColor.setOuDiaColor(line.split("=", -1)[1]);
                }
                if(line.split("=",-1)[0].equals("DiaJikuColor")){
                    diaJikuColor.setOuDiaColor(line.split("=", -1)[1]);
                }
                if(line.split("=",-1)[0].equals("EkimeiLength")){
                    stationNameLength=Integer.parseInt(line.split("=", -1)[1]);
                }
                if(line.split("=",-1)[0].equals("JikokuhyouRessyaWidth")){
                    trainWidth =60*Integer.parseInt(line.split("=", -1)[1]);
                }
                if(line.split("=",-1)[0].equals("AnySecondIncDec1")){
                    anySecondIncDec1=Integer.parseInt(line.split("=", -1)[1]);
                }
                if(line.split("=",-1)[0].equals("AnySecondIncDec2")){
                    anySecondIncDec2=Integer.parseInt(line.split("=", -1)[1]);
                }
                if(line.split("=",-1)[0].equals("FileType")){
                   fileType =line.split("=", -1)[1];
                }


            }
        }catch(Exception e1){
            e1.printStackTrace();
        }
    }

    /**
     * oudiaファイルを書き出す
     * @return
     */
    public void makeOuDiaText(File file,boolean oudiaSecond){
        StringBuilder a =new StringBuilder();
        try {
            a.append("FileType=");
            a.append(fileType);;
            a.append("\r\nRosen.\r\nRosenmei=");
            a.append(lineName);
            a.append("\r\n");
            for(int i=0;i<getStationNum();i++){
                a.append(station.get(i).makeStationText(oudiaSecond));
            }
            for(int i=0;i<getTypeNum();i++){
                a.append(trainType.get(i).makeTrainTypeText());
            }
            for(int dia=0;dia<diaName.size();dia++){
                a.append("Dia.\r\nDiaName=");
                a.append(diaName.get(dia));
                a.append("\r\nKudari.\r\n");
                for(int i=0;i<train.get(dia)[0].size();i++){
                    a.append(train.get(dia)[0].get(i).makeTrainText(0));
                }
                a.append("\r\n.\r\nNobori.\r\n");
                for(int i=0;i<train.get(dia)[1].size();i++){
                    a.append(train.get(dia)[1].get(i).makeTrainText(1));

                }
                a.append("\r\n.\r\n.\r\n");
            }
            a.append("KitenJikoku=");
            a.append(diagramStartTime/3600);
            a.append(String.format("%02d",(diagramStartTime/60)%60));
            a.append("\r\nDiagramDgrYZahyouKyoriDefault=").append(zahyouKyoriDefault);
            a.append("\r\nComment=").append(comment.replace("\n","\\n"));
            a.append("\r\n.\r\nDispProp.");
            for(int i=0;i<jikokuhyouFont.size();i++){
                Font font=jikokuhyouFont.get(i);
                a.append("\r\nJikokuhyouFont=");
                a.append(font.font2OudiaFontTxt());
            }
            a.append("\r\nJikokuhyouVFont=");
            a.append(jikokuVFont.font2OudiaFontTxt());
            a.append("\r\nDiaEkimeiFont=");
            a.append(diaEkimeiFont.font2OudiaFontTxt());
            a.append("\r\nDiaJikokuFont=");
            a.append(diaJikokuFont.font2OudiaFontTxt());
            a.append("\r\nDiaRessyaFont=");
            a.append(diaRessyaFont.font2OudiaFontTxt());
            a.append("\r\nCommentFont=");
            a.append(commentFont.font2OudiaFontTxt());

            a.append("\r\nDiaMojiColor=");
            a.append(diaMojiColor.getOudiaString());
            a.append("\r\nDiaHaikeiColor=");
            a.append(diaHaikeiColor.getOudiaString());
            a.append("\r\nDiaRessyaColor=");
            a.append(diaResyaColor.getOudiaString());
            a.append("\r\nDiaJikuColor=");
            a.append(diaJikuColor.getOudiaString());
            a.append("\r\nEkimeiLength=");
            a.append(stationNameLength);
            a.append("\r\nJikokuhyouRessyaWidth=");
            a.append(trainWidth /60);
            a.append("\r\nAnySecondIncDec1=");
            a.append(anySecondIncDec1);
            a.append("\r\nAnySecondIncDec2=");
            a.append(anySecondIncDec2);
            a.append("\r\n.");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try{
            PrintWriter writer    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"Shift_JIS")));

            //write contents of StringBuffer to a file
            writer.write(a.toString());
            //close the stream
            writer.close();        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 駅数を返す。
     * @return size of station(ArrayList)
     */
    protected int getStationNum(){
        return station.size();
    }

    /**
     * 種別の数を返す。
     * @return size of trainTyle
     */
    public int getTypeNum(){
        return trainType.size();
    }


    public int getOperationNum(int i){
        return 0;
    }
    public Operation getOperation(int i,int j){
        return null;
    }

}
