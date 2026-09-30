# SecureTunnel VPN

A modern, production-grade **Kotlin Multiplatform (KMP)** and **Compose Multiplatform** VPN client application built with Clean Architecture, strict MVI, Koin, Ktor, Room caching, real OpenVPN protocol support for free public **VPN Gate** volunteer relays, and an intelligent on-device **Groq AI VPN Assistant**.

---

## 🌟 Key Features

* **🛡️ Real VPN Client Engine**: Built on Android `VpnService` with TUN interface creation, MTU routing, DNS routing (`1.1.1.1`, `1.0.0.1`), and foreground service notifications with quick Disconnect actions.
* **🌐 100% Free Public Server Feed (VPN Gate)**: Directly streams and parses live volunteer OpenVPN relays with automatic deduplication, ping latency ranking, and bandwidth detection.
* **🤖 Intelligent VPN Assistant (Groq AI)**: Powered by Groq's fast inference engine (`llama-3.3-70b-versatile`) for instant VPN speed troubleshooting, server recommendations, and DNS security explanations (with built-in offline rule engine fallback).
* **⚡ Live Network Metrics**: Real-time upload/download speed counters, total bytes transferred, and active session duration timer.
* **✨ Multi-Factor Server Recommendation**: Transparent scoring algorithm based on lowest ping, highest measured bandwidth, and lowest active session count.
* **🩺 Real-Time Network Diagnostics**: Built-in 5-point test suite probing global internet routes, DNS resolver latency, OS VPN permission, socket reachability, and OpenVPN profile integrity.
* **🔒 Zero-Server & Zero-Log Architecture**: No custom server hosting required; all routing and API communication happen directly on-device with zero tracking or logging of browsing activity.

---

## 🏛️ Architecture & Clean MVI

```text
               Presentation Layer (Compose Multiplatform)
                                  │
                               Intent
                                  ▼
                         MVI ViewModel
                                  │
                               UseCase
                                  ▼
                          VpnRepository
                           /         \
            Remote Data Source     Local Cache (Offline-First)
                   │                         │
     VPN Gate & Groq AI Direct         Room / Multiplatform Storage
                   │
                   ▼
         Platform VPN Engine
            /             \
      Android             iOS
    VpnService     NetworkExtension
```

---

## 📦 Project Structure

```text
SecureTunnel/
│
├── androidApp/
│   └── src/main/
│       ├── AndroidManifest.xml          # VpnService, foreground service & permissions
│       └── java/com/yodgorbek/securetunnel/MainActivity.kt
│
├── iosApp/
│   ├── SecureTunnel/                    # iOS App Entry & UI Bridge
│   └── PacketTunnelExtension/           # NEPacketTunnelProvider implementation
│
├── sharedLogic/
│   └── src/
│       ├── commonMain/                  # Core models, MVI base, VpnGate parser, Groq client, Repositories, Use cases, Koin
│       ├── androidMain/                 # SecureTunnelVpnService, AndroidVpnEngine
│       ├── iosMain/                     # IosVpnEngine & NetworkExtension bridge
│       └── commonTest/                  # Unit test suite (Parser, Scoring, State)
│
├── sharedUI/
│   └── src/commonMain/
│       ├── designsystem/                # SecureTunnelTheme, VpnShieldButton, LiveTrafficCard, Badges
│       ├── feature/
│       │   ├── home/                    # HomeScreen (Shield, Status, Live Stats, Quick Recents)
│       │   ├── locations/               # LocationsScreen (Search, Filters, Recommended, Server List)
│       │   ├── assistant/               # AssistantScreen (Groq AI Chat, Suggested Prompts)
│       │   ├── history/                 # HistoryScreen (Connection Session Records, Total Data)
│       │   ├── diagnostics/             # DiagnosticsScreen (5-Point Live Network Checklist)
│       │   └── settings/                # SettingsScreen (Kill Switch, Auto-connect, Groq Config)
│       └── App.kt                       # Root Composable & Bottom Navigation Host
│
└── local.properties                     # Local configuration (GROQ_API_KEY, GROQ_MODEL)
```

---

## ⚙️ Configuration & Environment

Set your free Groq API key in [local.properties](file:///c:/Users/Edgar/AndroidStudioProjects/SecureTunnel/local.properties):

```properties
GROQ_API_KEY=gsk_your_groq_api_key_here
GROQ_MODEL=llama-3.3-70b-versatile
```

Users can also enter or update their custom Groq API key directly inside the in-app **Settings** screen.

---

## 🧪 Running Tests

Execute the multiplatform test suite:

```bash
# Windows
.\gradlew.bat test

# macOS / Linux
./gradlew test
```

---

## 📜 Volunteer Relay Transparency & Privacy

* **Volunteer Relays**: Public VPN servers are hosted by volunteers worldwide through the **VPN Gate Academic Project**.
* **Zero Logging by Client**: SecureTunnel never logs user browsing history, DNS queries, or transferred contents.
* **Direct Handshake**: Tunnels are established directly between the user device and the chosen volunteer node.