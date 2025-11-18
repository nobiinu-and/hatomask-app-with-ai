import sharp from 'sharp';
import { mkdirSync, existsSync } from 'fs';
import { join } from 'path';
import { program } from 'commander';

interface ImageConfig {
  name: string;
  width: number;
  height: number;
  format: 'png' | 'jpeg';
  text?: string;
  backgroundColor?: string;
  textColor?: string;
}

const DEFAULT_CONFIGS: ImageConfig[] = [
  // 小サイズ
  { name: 'small-square', width: 100, height: 100, format: 'png', text: '100×100', backgroundColor: '#3498db', textColor: '#ffffff' },
  { name: 'small-portrait', width: 100, height: 150, format: 'png', text: '100×150', backgroundColor: '#2ecc71', textColor: '#ffffff' },
  { name: 'small-landscape', width: 150, height: 100, format: 'png', text: '150×100', backgroundColor: '#e74c3c', textColor: '#ffffff' },
  
  // 中サイズ
  { name: 'medium-square', width: 500, height: 500, format: 'png', text: '500×500', backgroundColor: '#9b59b6', textColor: '#ffffff' },
  { name: 'medium-portrait', width: 400, height: 600, format: 'png', text: '400×600', backgroundColor: '#f39c12', textColor: '#ffffff' },
  { name: 'medium-landscape', width: 600, height: 400, format: 'png', text: '600×400', backgroundColor: '#1abc9c', textColor: '#ffffff' },
  
  // 大サイズ
  { name: 'large-square', width: 1000, height: 1000, format: 'jpeg', text: '1000×1000', backgroundColor: '#34495e', textColor: '#ffffff' },
  { name: 'large-portrait', width: 800, height: 1200, format: 'jpeg', text: '800×1200', backgroundColor: '#e67e22', textColor: '#ffffff' },
  { name: 'large-landscape', width: 1200, height: 800, format: 'jpeg', text: '1200×800', backgroundColor: '#95a5a6', textColor: '#ffffff' },
  
  // 特殊サイズ
  { name: 'very-small', width: 50, height: 50, format: 'png', text: '50×50', backgroundColor: '#16a085', textColor: '#ffffff' },
  { name: 'very-large', width: 2000, height: 2000, format: 'jpeg', text: '2000×2000', backgroundColor: '#c0392b', textColor: '#ffffff' },
  { name: 'ultra-wide', width: 1920, height: 500, format: 'jpeg', text: '1920×500', backgroundColor: '#2c3e50', textColor: '#ffffff' },
  
  // JPEG形式の例
  { name: 'jpeg-sample', width: 640, height: 480, format: 'jpeg', text: 'JPEG 640×480', backgroundColor: '#8e44ad', textColor: '#ffffff' },
];

function hexToRgb(hex: string): { r: number; g: number; b: number } {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return result
    ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16),
      }
    : { r: 204, g: 204, b: 204 };
}

async function generateImage(config: ImageConfig, outputDir: string): Promise<void> {
  const bgColor = hexToRgb(config.backgroundColor || '#cccccc');
  const fileName = `${config.name}.${config.format}`;
  const filePath = join(outputDir, fileName);

  // SVGでテキストと背景を生成
  const svg = `
    <svg width="${config.width}" height="${config.height}">
      <rect width="${config.width}" height="${config.height}" fill="${config.backgroundColor || '#cccccc'}"/>
      <rect x="1" y="1" width="${config.width - 2}" height="${config.height - 2}" 
            fill="none" stroke="${config.textColor || '#000000'}" stroke-width="2"/>
      ${config.text ? `
        <text x="${config.width / 2}" y="${config.height / 2 - 10}" 
              font-family="sans-serif" font-size="${Math.min(config.width, config.height) * 0.1}" 
              font-weight="bold" fill="${config.textColor || '#000000'}" 
              text-anchor="middle" dominant-baseline="middle">
          ${config.text}
        </text>
        <text x="${config.width / 2}" y="${config.height / 2 + Math.min(config.width, config.height) * 0.08}" 
              font-family="sans-serif" font-size="${Math.min(config.width, config.height) * 0.05}" 
              fill="${config.textColor || '#000000'}" 
              text-anchor="middle" dominant-baseline="middle">
          ${config.name}
        </text>
      ` : ''}
    </svg>
  `;

  // Sharpで画像を生成
  const sharpInstance = sharp(Buffer.from(svg));

  if (config.format === 'jpeg') {
    await sharpInstance.jpeg({ quality: 90 }).toFile(filePath);
  } else {
    await sharpInstance.png().toFile(filePath);
  }

  console.log(`✓ Generated: ${fileName} (${config.width}×${config.height})`);
}

async function main(): Promise<void> {
  program
    .name('generate-test-images')
    .description('Generate test images for Hatomask application')
    .option('-o, --output <dir>', 'Output directory', './generated')
    .option('-c, --custom <json>', 'Custom image configurations as JSON')
    .option('-w, --width <number>', 'Image width (use with --height and --format)')
    .option('-h, --height <number>', 'Image height (use with --width and --format)')
    .option('-f, --format <type>', 'Image format: png or jpeg')
    .option('-n, --name <name>', 'Image name (without extension)')
    .option('--no-default', 'Skip generating default images')
    .parse(process.argv);

  const options = program.opts();
  const outputDir = options.output;

  // 出力ディレクトリを作成
  if (!existsSync(outputDir)) {
    mkdirSync(outputDir, { recursive: true });
    console.log(`Created output directory: ${outputDir}`);
  }

  let configs: ImageConfig[] = [];

  // カスタム設定がある場合
  if (options.custom) {
    try {
      const customConfigs = JSON.parse(options.custom);
      configs = Array.isArray(customConfigs) ? customConfigs : [customConfigs];
    } catch (error) {
      console.error('Error parsing custom configuration:', error);
      process.exit(1);
    }
  }

  // 個別の引数で指定された場合
  if (options.width && options.height && options.format) {
    const width = parseInt(options.width, 10);
    const height = parseInt(options.height, 10);
    const format = options.format.toLowerCase();

    if (isNaN(width) || isNaN(height)) {
      console.error('Error: Width and height must be valid numbers');
      process.exit(1);
    }

    if (format !== 'png' && format !== 'jpeg') {
      console.error('Error: Format must be either "png" or "jpeg"');
      process.exit(1);
    }

    const name = options.name || `custom-${width}x${height}`;
    const customConfig: ImageConfig = {
      name,
      width,
      height,
      format: format as 'png' | 'jpeg',
      text: `${width}×${height}`,
      backgroundColor: '#3498db',
      textColor: '#ffffff',
    };

    configs.push(customConfig);
  }

  // デフォルト画像を生成するかどうか
  if (options.default !== false && configs.length === 0) {
    configs = DEFAULT_CONFIGS;
  } else if (options.default !== false && configs.length > 0) {
    // カスタム設定とデフォルト設定を両方使用
    configs = [...configs, ...DEFAULT_CONFIGS];
  }

  console.log(`\nGenerating ${configs.length} test images...\n`);

  // 画像を生成
  for (const config of configs) {
    try {
      await generateImage(config, outputDir);
    } catch (error) {
      console.error(`✗ Failed to generate ${config.name}:`, error);
    }
  }

  console.log(`\n✓ All images generated successfully in: ${outputDir}\n`);
}

main();
