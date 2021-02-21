# TalkWithNearPlayer
[![EasySpigotAPI](https://img.shields.io/badge/EasySpigotAPI-%E2%AC%85-4D4.svg)](https://github.com/sya-ri/EasySpigotAPI)

## 機能

### 自動部屋分けモード
[![Youtubeで開く](http://img.youtube.com/vi/xTFWXIlT1g4/0.jpg)](http://www.youtube.com/watch?v=xTFWXIlT1g4 "")

部屋のオーナープレイヤーを設定することでそのプレイヤーの付近では話せるようになります。

### アイテムを使ったミュート切り替えモード
[![Youtubeで開く](http://img.youtube.com/vi/ZSiLYEWhbqg/0.jpg)](http://www.youtube.com/watch?v=ZSiLYEWhbqg "")

専用のアイテムでプレイヤーをクリックすることでミュートを切り替えることができます。

## 導入

1. https://discord.com/developers/applications でボットを作成する。
    1. `New Application` をクリック。
    2. `NAME` にボットの名前を入力。
    3. 左のタブから `Bot` を選択。
    4. `Add Bot` を押す。
    5. `TOKEN` セクションの `Copy` をクリックしてどこかにメモっておく。(`config.yml` の `discord.token` に入力する。)
    6. 左のタブから `OAuth2` を選択。
    7. `SCOPES` から `bot` を選択。
    8. `BOT PERMISSIONS` から `Administrator` を選択。
    9. `SCOPES` に表示されているURLをコピーし、アクセスする。
    10. 導入するDiscordサーバーを選択する。
2. プラグインをダウンロードする。
3. プラグインをサーバーに導入する。
4. `TalkWithNearPlayer/config.yml` に必要な項目を入力する。
    `discord.token` に `1.5` のトークンを入力する。
    その他のコンフィグは自由に設定をする。コマンドで設定することも可能。
5. サーバーを起動する。
6. 機能を利用するには必ずどこかのボイスチャンネルに入っている必要があるので注意する。

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
  radius: 5 # 声が聞こえる半径
  player: [] # 部屋のオーナー
```

### player.yml
```yml
<name>: # uuid と一致している必要はなく、好きに決めて良い。
  uuid: "" # プレイヤーのUUID
  discord: 0 # 連携するDiscordアカウントのID
```

## コマンド

### /twnp
`/talk-with-near-player` というコマンドの省略形です。通常プレイヤー向けのコマンドです。

| コマンド | 説明 |
|--------|------|
| /twnp connect | Discordと連携します。|
| /twnp disconnect | Discordとの連携を解除します。|

### /twnpa
`/talk-with-near-player-admin` というコマンドの省略形です。設定・管理用のコマンドです。

| 権限名 | 説明 |
|-------|-----|
| twnp.admin | `/twnpa` コマンドの実行権限 |

| コマンド | 説明 |
|--------|------|
| /twnpa mode | モードの切り替えが可能です。|
| /twnpa mode Auto | 自動部屋分けモードになります。|
| /twnpa mode Item | アイテムを使ったミュート切り替えモードになります。|
| /twnpa item | ミュート切り替え用のアイテムを入手します。|
| /twnpa auto radius | 自動部屋分けモードの声が聞こえる範囲を設定します。|
| /twnpa auto player add | 自動部屋分けモードのオーナーを追加します。|
| /twnpa auto player remove | 自動部屋分けモードのオーナーを削除します。|
| /twnpa auto player list | 自動部屋分けモードのオーナー一覧を表示します。|
| /twnpa reload | コンフィグのリロードをします。|

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