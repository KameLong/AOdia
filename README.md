AOdia
====
## OverView
AOdia is Japanese Android App of train diagram.
AOdiaは鉄道ダイヤグラムに関する日本語のアンドロイドアプリです。

## Description
鉄道・バスなどの時刻表の閲覧をするためのアプリです。
時刻表を作成、編集するものとして、WindowsソフトのOuDiaというものがあり、AOdiaはOuDiaのデータファイルの読み込みに対応しています。
AndroidでOuDiaのファイルを閲覧を可能にするアプリだと思ってください。

## このBranchについて



## Author
カメロング
　twitter @LongKame
　email kamelong.dev@gmail.com
  HP http://kamelong.web.fc2.com/aodia/

## プロジェクト内ソースコードについての説明
AOdiaはFragmentを組み合わせて表示するアプリです。
Fragmentの種類として
+ MenuFragment:メニューを表示するためのものです
+ SettingFragment:設定画面を表示するためのものです
+ HelpFragment:ヘルプを表示するためのものです
+ CommentFragment:ダイヤファイル内のコメントを表示するためのものです
+ TimeTableFragment:路線時刻表を表示するためのものです
+ DiagramFragment:ダイヤグラム画面を表示するためのものです
+ StationInfoFragment:駅時刻表目次を表示するためのものです
+ StationInfoFragment:駅時刻表を表示するためのものです

これらのFragmentを画面内に重ねて表示します
なお
MenuFragmentとHelpFragment,SettingFragmentは他とは異なる特殊なFragmentで、
MenuFragmentは画面左端からスワイプすることで出現し、Help-Settingは全画面表示のみ対応します。


Fragment用の一つのサブパッケージの中には、役割の似た１つないし複数のFragmentとFragment内に用いる独自Viewが含まれています。

### その他パッケージの説明
#### oudia
 oudiaなどの１路線の時刻表データを保持するためのものです。
 DiaFileという抽象クラスがあるので、その中に詳しい説明が書かれています
#### datebase
 アプリで保管したいデータがある場合、データベースを用います。
 このディレクトリ内にはデータベースに関するクラスがあります。
#### file
　ファイル選択ダイアログに関するクラスがあります。

## 今後の展開
+ 駅検索機能をつけ、検索結果の駅時刻表を出せるようにする
+ ダイヤグラム編集機能を付ける

