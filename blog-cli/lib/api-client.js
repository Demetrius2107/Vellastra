import axios from 'axios';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const CONFIG_PATH = path.resolve(__dirname, '../config/default.json');

let config = JSON.parse(fs.readFileSync(CONFIG_PATH, 'utf-8'));

const client = axios.create({
  baseURL: config.apiBaseUrl,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 动态注入 Token
client.interceptors.request.use((req) => {
  if (config.token) {
    req.headers.Authorization = `Bearer ${config.token}`;
  }
  return req;
});

/**
 * 刷新配置（init 后调用）
 */
export function reloadConfig() {
  config = JSON.parse(fs.readFileSync(CONFIG_PATH, 'utf-8'));
  client.defaults.baseURL = config.apiBaseUrl;
}

/**
 * 保存配置
 */
export function saveConfig(updates) {
  config = { ...config, ...updates };
  fs.writeFileSync(CONFIG_PATH, JSON.stringify(config, null, 2), 'utf-8');
}

/**
 * 获取当前配置
 */
export function getConfig() {
  return { ...config };
}

/**
 * 创建文章
 */
export async function createArticle(data) {
  const res = await client.post('/article', data);
  return res.data;
}

/**
 * 更新文章
 */
export async function updateArticle(id, data) {
  const res = await client.put(`/article/${id}`, data);
  return res.data;
}

/**
 * 发布文章
 */
export async function publishArticle(id) {
  const res = await client.post(`/article/${id}/publish`);
  return res.data;
}

/**
 * 获取文章详情
 */
export async function getArticle(id) {
  const res = await client.get(`/article/${id}`);
  return res.data;
}

/**
 * 上传图片
 */
export async function uploadImage(filePath) {
  const FormData = (await import('form-data')).default;
  const form = new FormData();
  form.append('file', fs.createReadStream(filePath));
  const res = await client.post('/file/upload/image', form, {
    headers: form.getHeaders(),
  });
  return res.data;
}
