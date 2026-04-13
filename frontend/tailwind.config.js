/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        bg: '#000000',
        surface: '#0a0a0a',
        surface2: '#111111',
        surface3: '#1a1a1a',
        border: '#1f1f1f',
        border2: '#2a2a2a',
        border3: '#333333',
        text: '#ffffff',
        text2: '#a0a0a0',
        text3: '#505050',
        accent: '#ffffff',
        'accent-dim': '#1a1a1a',
        'status-ok': '#ffffff',
        'status-ok-dim': '#1a1a1a',
        'status-warn': '#888888',
        'status-warn-dim': '#161616',
        'status-risk': '#ffffff',
        'status-risk-dim': '#1f1f1f',
      },
      fontFamily: {
        sans: ['Onest', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
    },
  },
  plugins: [],
}
