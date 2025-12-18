# Hatomask App ドキュメント総目次

## 🚀 はじめに

- [プロジェクト概要](../README.md)
- [AI 協働開発手法](AI_COLLABORATION.md) - AI 協働開発の思想、原則、7 段階プロセス
- [AI 協働アーキテクチャ](../copilot-instructions.md)

## 👨‍💻 開発者向け

- [開発ガイド](dev/DEVELOPMENT.md) - TDD 開発フロー、テスト実行方法
- [コーディング規約](dev/CODING_STANDARDS.md) - 命名規則、設計原則
- [Docker 環境](dev/DOCKER.md) - コンテナ構成と起動方法
- [Linter 設定](dev/LINTER.md) - 静的解析ツールの設定

## 🤖 AI 協働向け

- [AI 協働ガイド](ai/README.md) - AI 活用の全体像と使い方
- [システムプロンプト](ai/prompts/system/) - AI の基本動作定義（作成予定）
- [タスクプロンプト](ai/prompts/tasks/) - タスク別指示テンプレート（作成予定）
- [作業ログ](ai/logs/) - AI 協働セッション記録（作成予定）

## 📖 仕様書

- [機能概要](spec/README.md) - 全機能の一覧と概要
- [機能仕様](spec/features/) - 詳細な機能仕様書
- [仕様テンプレート](spec/templates/feature.template.md)

## 📚 共有リソース

- [用語集](shared/) - プロジェクト用語の定義（作成予定）
- [アーキテクチャ決定記録](shared/) - 重要な設計判断の記録（作成予定）
- [トラブルシューティング](shared/) - よくある問題と解決策（作成予定）

## 📝 テスト

- テストリスト: `docs/plans/[Spec名]_[シナリオ識別子]_domain_testlist.md` / `api_testlist.md` - TDD 実装管理
- [E2E テスト](../e2e/README.md) - Playwright + Cucumber

## 🔗 外部リンク

- [GitHub Repository](https://github.com/nobiinu-and/hatomask-app-with-ai)
