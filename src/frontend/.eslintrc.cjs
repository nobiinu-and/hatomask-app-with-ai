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
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs', 'vite.config.ts'],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    project: ['./tsconfig.json', './tsconfig.node.json'],
    tsconfigRootDir: __dirname,
  },
  plugins: ['react-refresh', '@typescript-eslint', 'react'],
  settings: {
    react: {
      version: 'detect',
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

    // React Hooks
    'react-hooks/rules-of-hooks': 'error',
    'react-hooks/exhaustive-deps': 'warn',
  },
};
