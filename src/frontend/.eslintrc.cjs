module.exports = {
  root: true,
  env: {
    browser: true,
    es2020: true,
    node: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:@typescript-eslint/recommended-requiring-type-checking',
    'plugin:react/recommended',
    'plugin:react/jsx-runtime',
    'plugin:react-hooks/recommended',
    'plugin:jsx-a11y/recommended',
    'plugin:import/recommended',
    'plugin:import/typescript',
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs', 'vite.config.ts'],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    project: ['./tsconfig.json', './tsconfig.node.json'],
    tsconfigRootDir: __dirname,
  },
  plugins: ['react-refresh', '@typescript-eslint', 'react', 'jsx-a11y', 'import'],
  settings: {
    react: {
      version: 'detect',
    },
    'import/resolver': {
      typescript: true,
      node: true,
    },
  },
  rules: {
    // React Refresh
    'react-refresh/only-export-components': [
      'warn',
      { allowConstantExport: true },
    ],

    // TypeScript規約: any禁止
    '@typescript-eslint/no-explicit-any': 'error',
    '@typescript-eslint/no-unsafe-assignment': 'error',
    '@typescript-eslint/no-unsafe-member-access': 'error',
    '@typescript-eslint/no-unsafe-call': 'error',
    '@typescript-eslint/no-unsafe-return': 'error',

    // 命名規則
    '@typescript-eslint/naming-convention': [
      'error',
      // 型・インターフェース: PascalCase
      {
        selector: ['typeLike'],
        format: ['PascalCase'],
      },
      // 変数・関数: camelCase
      {
        selector: ['variable', 'function'],
        format: ['camelCase', 'PascalCase'], // PascalCaseはReactコンポーネント用
      },
      // 定数: UPPER_SNAKE_CASE
      {
        selector: 'variable',
        modifiers: ['const', 'global'],
        format: ['camelCase', 'PascalCase', 'UPPER_CASE'],
      },
    ],

    // コンポーネント設計
    'react/prop-types': 'off', // TypeScriptを使用するため不要
    'react/jsx-uses-react': 'off', // React 17+では不要
    'react/react-in-jsx-scope': 'off', // React 17+では不要

    // コード品質
    'no-console': ['warn', { allow: ['warn', 'error'] }], // console.log警告
    'no-debugger': 'error',
    'no-unused-vars': 'off', // TypeScript版を使用
    '@typescript-eslint/no-unused-vars': [
      'error',
      {
        argsIgnorePattern: '^_',
        varsIgnorePattern: '^_',
      },
    ],

    // コードスタイル
    'prefer-const': 'error',
    'no-var': 'error',
    'object-shorthand': 'error',
    'prefer-template': 'error',
    'prefer-arrow-callback': ['error', { allowNamedFunctions: false }],
    'prefer-destructuring': [
      'error',
      {
        array: true,
        object: true,
      },
      {
        enforceForRenamedProperties: false,
      },
    ],
    'prefer-spread': 'error',

    // セキュリティ
    'no-eval': 'error',
    'no-implied-eval': 'error',
    'no-new-func': 'error',
    '@typescript-eslint/no-implied-eval': 'error',

    // インポート
    'import/order': [
      'warn',
      {
        groups: [
          'builtin',
          'external',
          'internal',
          ['parent', 'sibling'],
          'index',
          'object',
          'type',
        ],
        'newlines-between': 'always',
        alphabetize: {
          order: 'asc',
          caseInsensitive: true,
        },
      },
    ],
    'import/no-unresolved': 'off', // TypeScriptでチェックするため無効化
    'import/default': 'off', // React 18のESMエクスポートで誤検知するため無効化
    'import/no-named-as-default': 'off', // React関連で誤検知するため無効化
    'import/no-named-as-default-member': 'off', // React関連で誤検知するため無効化
    'import/no-duplicates': 'error',
    'import/newline-after-import': 'error',

    // アクセシビリティ
    'jsx-a11y/alt-text': 'error',
    'jsx-a11y/aria-props': 'error',
    'jsx-a11y/aria-proptypes': 'error',
    'jsx-a11y/aria-unsupported-elements': 'error',
    'jsx-a11y/role-has-required-aria-props': 'error',
    'jsx-a11y/role-supports-aria-props': 'error',

    // React Hooks
    'react-hooks/rules-of-hooks': 'error',
    'react-hooks/exhaustive-deps': 'warn',

    // パフォーマンス（警告レベル）
    'react/jsx-no-bind': [
      'warn',
      {
        allowArrowFunctions: true,
        allowBind: false,
        ignoreRefs: true,
      },
    ],
  },
};
