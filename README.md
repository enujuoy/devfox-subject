
# Spring Boot 掲示板

## 機能とエンドポイント

### ユーザー機能

- 会員登録ページ
  - GET /users/join
  - アカウント登録が成功すると、成功メッセージを表示後にログイン画面へリダイレクト
  - ログイン中のユーザーは会員登録ページにアクセス不可
- 会員登録機能
  - POST /users/join
  - IDやニックネームが重複している、もしくはパスワードとパスワード確認が一致しない場合、会員登録不可
  - パスワードは暗号化して保存
  - 新規ユーザーはBRONZEランクに設定
- ログインページ
  - GET /users/login
  - ID（loginId）、パスワードでログイン
  - ログイン成功時に成功メッセージを表示後、前のページにリダイレクト
    - 前のページがない場合や会員登録ページの場合はホームへリダイレクト
  - ログイン中のユーザーはログインページにアクセス不可
- ログイン機能
  - POST /users/login
  - Spring Securityを使用してログイン処理
- マイページ
  - GET /users/myPage/{category}
  - ログインしていないユーザーはアクセス不可
  - ログイン中のユーザーの情報を確認可能
  - 会員情報（パスワード、ニックネーム）修正可能
  - 退会可能
    - 退会時に作成した投稿、いいね、コメントすべて削除
  - categoryに応じて、作成した投稿（board）、いいねした投稿（like）、コメントした投稿（comment）のリストを確認可能

### 掲示板機能

- 掲示板種類
  - 会員挨拶(GREETING)、自由掲示板(FREE)、ゴールド掲示板(GOLD)
- 投稿リストページ
  - GET /boards/{category}
  - 該当カテゴリの投稿リストを表示
  - 投稿作成ボタンをクリックすると該当カテゴリでの投稿作成ページに移動
  - 投稿をクリックすると該当投稿の詳細ページに移動
  - 1ページに10件の投稿を表示（通知除く）
  - タイトルや作成者で検索可能
  - 新着順、いいね順、コメント数順で並び替え可能（降順）
  - 該当掲示板の通知は常に上部に表示
  - ゴールド掲示板はGOLDランク以上のユーザーのみアクセス可能
- 投稿作成ページ
  - GET /boards/{category}/write
  - ログイン中のユーザーのみアクセス可能
  - タイトル、内容記入可能 + 画像アップロード可能（最大10MB）
  - 投稿作成成功時に投稿詳細ページにリダイレクト
  - ADMINランクのユーザーのみ通知作成可能
    - ADMINランクが作成した投稿は該当掲示板の通知として登録
- 投稿作成機能
  - POST /boards/{category}
  - 入力されたタイトル（title）、内容（body）で投稿を保存
  - 画像があれば画像も保存
  - 作成日、作成ユーザーも一緒に保存
  - ログイン中のユーザーのみ作成可能
- 投稿詳細ページ
  - GET /boards/{category}/{boardId}
  - boardIdに該当する投稿内容を表示
  - boardIdのカテゴリがcategoryと一致しない場合、エラー発生
  - 投稿者には修正、削除ボタンを表示
  - ADMINランクのユーザーには削除ボタンのみ表示
  - 左上にいいね数を表示
  - ログイン中のユーザーはハートボタンでいいね追加、再度クリックでいいね解除
  - ログイン中のユーザーはコメント作成可能で、自分が作成したコメントの修正、削除も可能
  - 画像があれば表示およびダウンロード可能
- 投稿修正機能
  - POST /boards/{category}/{boardId}/edit
  - 投稿詳細ページで修正ボタンをクリックして修正可能
  - タイトル、内容修正可能 + 画像修正可能
  - 投稿者のみ修正可能
  - 修正成功時にメッセージを表示し、該当投稿にリダイレクト
- 投稿削除機能
  - GET /boards/{category}/{boardId}/delete
  - 削除ボタンをクリック後、確認して削除
  - 投稿者およびADMINのみ削除可能
  - 削除成功時にメッセージを表示し、リストにリダイレクト
  - 会員挨拶は削除不可
  - 投稿削除時にいいねやコメントもすべて削除
  - いいねが削除されてもGOLDランクは維持
- コメント機能
  - コメント作成: POST /comments/{boardId}
  - コメント修正: POST /comments/{commentId}/edit
  - コメント削除: GET /comments/{commentId}/delete
  - ログイン中のユーザーのみコメント作成可能
  - 自分が作成したコメントの修正、削除可能
  - 投稿詳細ページ下部でコメント追加可能
- 検索機能
  - 投稿タイトル、作成者のニックネームで検索可能
  - 新着順（デフォルト）、いいね数、閲覧数順で（降順）並び替え可能
- ページング機能
  - 1ページに10件の投稿を表示
  - ボタンでページ移動可能
