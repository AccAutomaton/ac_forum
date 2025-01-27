import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import * as path from "node:path";

export default defineConfig(({mode}) => {
    const env = loadEnv(mode, process.cwd())
    return {
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
        define: {
            'API_HOST': JSON.stringify(env.VITE_API_HOST),
        }
    }
})
