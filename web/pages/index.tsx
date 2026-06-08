import React, { useState } from 'react';

export default function ClipForgeWebDashboard() {
  const [activeTab, setActiveTab] = useState('home');
  const [url, setUrl] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);
  const [progress, setProgress] = useState(0);

  const startPipeline = () => {
    setIsProcessing(true);
    let curr = 0;
    const interval = setInterval(() => {
      curr += 10;
      setProgress(curr);
      if (curr >= 100) {
        clearInterval(interval);
        setIsProcessing(false);
        alert("A.I. clipping successfully extracted 4 viral short tracks!");
      }
    }, 800);
  };

  return (
    <div className="min-h-screen bg-neutral-950 text-neutral-100 font-sans flex flex-col md:flex-row">
      
      {/* SIDEBAR NAVIGATION HEADER */}
      <aside className="w-full md:w-64 bg-neutral-900 border-r border-neutral-800 p-6 flex flex-col gap-6">
        <div>
          <h1 className="text-xl font-black tracking-tighter text-violet-500 flex items-center gap-2">
            <span>⚡</span> CLIPFORGE AI
          </h1>
          <p className="text-xs text-neutral-450 mt-1">SaaS Video Workspace</p>
        </div>

        <nav className="flex flex-col gap-2 flex-grow">
          {[
            { id: 'home', label: 'Home Studio', icon: '🏠' },
            { id: 'projects', label: 'Source Projects', icon: '📁' },
            { id: 'studio', label: 'Clipper Studio', icon: '🎬' },
            { id: 'analytics', label: 'Analytics Insights', icon: '📈' },
            { id: 'settings', label: 'System Preferences', icon: '⚙️' },
          ].map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full text-left py-3 px-4 rounded-lg font-semibold text-sm flex items-center gap-3 transition-all ${
                activeTab === item.id
                  ? 'bg-violet-600/10 border border-violet-500/20 text-violet-400'
                  : 'text-neutral-400 hover:bg-neutral-800/50 hover:text-neutral-100'
              }`}
            >
              <span>{item.icon}</span>
              {item.label}
            </button>
          ))}
        </nav>

        <div className="border-t border-neutral-800 pt-4 text-[10px] text-neutral-500">
          User: guest_dev@clipforge.ai
        </div>
      </aside>

      {/* MAIN COCKPIT VIEW WINDOW */}
      <main className="flex-grow p-8 flex flex-col gap-6 overflow-y-auto">
        
        {/* TAB 1: HOME PANEL */}
        {activeTab === 'home' && (
          <div className="flex flex-col gap-6 animate-fadeIn">
            <div>
              <h2 className="text-2xl font-bold">Import Long-Form Videos</h2>
              <p className="text-neutral-400 text-sm">Convert podcasts, streams, and film reels to viral vertical Shorts instantly.</p>
            </div>

            {isProcessing && (
              <div className="bg-neutral-900 border border-cyan-500/20 rounded-xl p-5 flex flex-col gap-3">
                <div className="flex justify-between items-center text-sm">
                  <span className="font-bold text-cyan-400">Processing Audio Extraction & Whisper Transcribing...</span>
                  <span className="font-black text-cyan-400">{progress}%</span>
                </div>
                <div className="w-full bg-neutral-800 h-2 rounded-full overflow-hidden">
                  <div className="bg-cyan-500 h-full transition-all duration-300" style={{ width: `${progress}%` }}></div>
                </div>
              </div>
            )}

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              
              {/* VIDEO URL FORM */}
              <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 flex flex-col gap-4">
                <h3 className="font-bold text-md text-violet-400">Import from YouTube URL</h3>
                
                <input
                  type="text"
                  placeholder="Paste YouTube, TikTok or Vimeo watch links..."
                  value={url}
                  onChange={(e) => setUrl(e.target.value)}
                  className="w-full bg-neutral-950 border border-neutral-800 rounded-lg py-3 px-4 text-sm focus:outline-none focus:border-violet-500 text-neutral-100"
                />

                <div className="flex gap-4">
                  <label className="flex items-center gap-2 text-neutral-400 text-xs cursor-pointer">
                    <input type="checkbox" className="accent-violet-600" defaultChecked />
                    Smart Face-Tracking Reframing
                  </label>
                  <label className="flex items-center gap-2 text-neutral-400 text-xs cursor-pointer">
                    <input type="checkbox" className="accent-violet-600" />
                    Movie Spoiler Detection Warning
                  </label>
                </div>

                <button
                  onClick={startPipeline}
                  disabled={isProcessing}
                  className="w-full py-3 bg-violet-600 hover:bg-violet-500 disabled:opacity-50 text-neutral-100 font-bold text-sm rounded-lg transition-all"
                >
                  START AI COGNITIVE CUTTER
                </button>
              </div>

              {/* DEMO INGEST DRAG & DROP BOX */}
              <div className="bg-neutral-900 border border-neutral-800 border-dashed rounded-xl p-6 flex flex-col items-center justify-center gap-4 cursor-pointer hover:border-violet-500/40 transition-all">
                <span className="text-3xl">📤</span>
                <div className="text-center">
                  <p className="font-bold text-sm">Drag and drop raw video files here</p>
                  <p className="text-xs text-neutral-400 mt-1">Supports MP4, MOV, FLV up to 10GB size limits.</p>
                </div>
              </div>

            </div>
          </div>
        )}

        {/* TAB 3: CLIPPER WORKSTATION PREVIEW STUDIO */}
        {activeTab === 'studio' && (
          <div className="flex flex-col lg:flex-row gap-6 h-[calc(100vh-8.5rem)] min-h-[500px]">
            
            {/* COMPACT VERTICAL PLAYER CANVAS */}
            <div className="lg:w-[400px] bg-neutral-900 border border-neutral-800 rounded-xl p-5 flex flex-col gap-4 justify-between">
              <span className="text-xs font-black text-violet-400 tracking-wider">VERTICAL 9:16 PREVIEW</span>
              
              <div className="flex-grow bg-black rounded-lg border border-neutral-800 flex items-center justify-center relative overflow-hidden">
                <div className="w-[180px] h-[320px] bg-neutral-950 border border-neutral-800 shadow-2xl relative flex flex-col justify-end p-4">
                  {/* Face Tracker Overlay */}
                  <div className="absolute inset-x-2 top-20 bottom-16 border-2 border-dashed border-cyan-400/80 rounded-lg flex items-start p-2">
                     <span className="text-[7px] text-cyan-400 bg-neutral-950/80 px-1 rounded font-bold">Face Focus Box</span>
                  </div>

                  {/* Caption Highlights Over Video */}
                  <div className="z-10 text-center mb-6">
                     <span className="bg-yellow-400 text-black px-2 py-1 font-black text-[10px] rounded border border-black shadow">
                       WE MUST DECOUPLE TIME FROM EARNINGS!
                     </span>
                  </div>

                  {/* Banner CTA Overlay */}
                  <div className="z-10 bg-red-600 text-center py-1 rounded text-white font-black text-[7px] tracking-wider uppercase">
                     WATCH COMPANION SHORTS IN BIO
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-3 bg-neutral-950 p-3 rounded-lg border border-neutral-800 text-xs">
                <button className="text-violet-500 hover:text-violet-400">▶ PLAY</button>
                <div className="flex-grow bg-neutral-800 h-1 rounded-full overflow-hidden">
                  <div className="bg-violet-500 h-full w-1/3"></div>
                </div>
                <span className="text-neutral-450">00:14.2 / 00:45.0</span>
              </div>
            </div>

            {/* PIPELINE CAPTIONS & METRICS CONFIG PANELS */}
            <div className="flex-grow bg-neutral-900 border border-neutral-800 rounded-xl p-6 flex flex-col gap-6">
              <div className="flex justify-between items-center border-b border-neutral-800 pb-4">
                <div>
                  <h3 className="font-bold text-md">Extracted Moments Workstation</h3>
                  <p className="text-xs text-neutral-450 mt-1">Select and customize active word designs and descriptions.</p>
                </div>
                <button className="bg-violet-600 hover:bg-violet-500 text-neutral-100 px-4 py-2 text-xs font-bold rounded-lg transition-all">
                  📥 COMPILE & DOWNLOAD FULL MP4
                </button>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 flex-grow overflow-y-auto">
                <div className="flex flex-col gap-4">
                  <h4 className="font-bold text-sm text-neutral-400">Captions Style Presets</h4>
                  <div className="grid grid-cols-2 gap-3">
                    {['TikTok Bold', 'Alex Hormozi Yellow', 'MrBeast Comic', 'Minimal Clear'].map((style, idx) => (
                      <div key={idx} className="p-3 bg-neutral-950 border border-neutral-800 rounded-lg cursor-pointer hover:border-violet-500 transition-all">
                        <p className="font-bold text-xs">{style}</p>
                        <p className="text-[10px] text-neutral-500 mt-1">Prebuild styled template subtitle overlays.</p>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="flex flex-col gap-4">
                  <h4 className="font-bold text-sm text-neutral-400">A.I. Generated Hooks</h4>
                  <div className="flex flex-col gap-2">
                    {[
                      'Nobody Expected This Reactor Fail...',
                      'Stop Working For Standard Base Salaries!',
                      'This 1 Single Mindset Decouples Wealth Output',
                    ].map((hook, idx) => (
                      <div key={idx} className="p-3 bg-neutral-950 border border-neutral-800 rounded-lg cursor-pointer hover:bg-neutral-850">
                        <p className="text-xs font-bold text-pink-500">✨ {hook}</p>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>

          </div>
        )}

        {/* OTHER FALLBACK TABS */}
        {(activeTab === 'projects' || activeTab === 'analytics' || activeTab === 'settings') && (
          <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-12 text-center flex flex-col items-center gap-4">
            <span className="text-4xl text-neutral-500">🧱</span>
            <div>
              <h3 className="font-bold text-lg text-neutral-200">View Active Dashboard panel</h3>
              <p className="text-neutral-400 text-sm mt-1">Full configurations are currently synchronized on the Android companion emulator. Enjoy interactive pipelines live.</p>
            </div>
          </div>
        )}

      </main>
    </div>
  );
}
