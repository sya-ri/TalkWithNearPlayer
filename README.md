# TalkWithNearPlayer
[![EasySpigotAPI](https://img.shields.io/badge/EasySpigotAPI-%E2%AC%85-4D4.svg)](https://github.com/sya-ri/EasySpigotAPI)

## 機能

### 自動部屋分けモード
[![Youtubeで開く](http://img.youtube.com/vi/xTFWXIlT1g4/0.jpg)](http://www.youtube.com/watch?v=xTFWXIlT1g4 "")

部屋のオーナープレイヤーを設定することでそのプレイヤーの付近では話せるようになります。

### アイテムを使ったミュート切り替えモード
[![Youtubeで開く](http://img.youtube.com/vi/ZSiLYEWhbqg/0.jpg)](http://www.youtube.com/watch?v=ZSiLYEWhbqg "")

専用のアイテムでプレイヤーをクリックすることでミュートを切り替えることができます。

## コンフィグ

### config.yml
```yml
discord:
  token: "" # ボットのトークン
  guild: 0 # 稼働させるDiscordサーバーのID
mode: Item # モード(Auto, Item)
item: # mode == Item : ミュート切り替えアイテムの設定
  type: SLIME_BALL # アイテムマテリアル
  name: "&aミュート切り替え" # アイテム名
auto: # mode == Auto : 自動部屋移動の設定
  radius: 5 # プレイヤーを部屋に入れる半径
  player: [] # 部屋のオーナー
```

### player.yml
```yml
<name>: # uuid と一致している必要はなく、好きに決めて良い。
  uuid: "" # プレイヤーのUUID
  discord: 0 # 連携するDiscordアカウントのID
```

## コマンド

| コマンド | 説明 |
|--------|------|
| /twnp mode | モードの切り替えが可能です。|
| /twnp mode Auto | 自動部屋分けモードになります。|
| /twnp mode Item | アイテムを使ったミュート切り替えモードになります。|
| /twnp item | ミュート切り替え用のアイテムを入手します。|
| /twnp auto | 自動部屋分けモードのオーナー一覧を表示します。|
| /twnp reload | コンフィグのリロードをします。|

## 開発者向け

### Gradle Task

#### ktlintFormat
```
gradle ktlintFormat
```

ソースコードを綺麗にすることができます。

#### addKtlintFormatGitPreCommitHook
```
gradle addKtlintFormatGitPreCommitHook
```

Git Commit する時に `ktlintFormat` を実行します。やっておくことで必ずフォーマットしてくれるようになるので忘れがちな人にオススメです。

#### shadowJar
```
gradle shadowJar
```

Jar ファイルを生成します。`build/libs` フォルダの中に生成されます。