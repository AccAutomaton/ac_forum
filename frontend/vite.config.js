import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import * as path from "node:path";

export default defineConfig({
    plugins: [
        vue(),
    ],
    server: {
        hmr: true,
        port: 9001
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src')
        },
    },
})
