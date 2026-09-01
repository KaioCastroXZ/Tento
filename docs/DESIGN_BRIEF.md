# Tento — Placar de Truco · Briefing de Design

> Documento para enviar ao **Claude Design**. O objetivo é receber de volta um
> `.md` com uma proposta de **melhoria de design** do app (direção visual,
> hierarquia, layout tela a tela, escala tipográfica, uso de cor, movimento),
> mantendo as restrições abaixo e podendo ser implementada em **Jetpack Compose +
> Material 3**.

---

## 1. O que é o app

**Tento** é um **placar digital de Truco** para partidas presenciais. Substitui o
marcador físico (o risquinho na mesa). **Não** simula o jogo de cartas, não
distribui cartas, não joga contra o celular.

- **Usuário:** alguém sentado à mesa jogando Truco, que precisa marcar os tentos
  rápido, muitas vezes **com uma mão só**, enquanto segura as cartas com a outra.
- **Contexto:** ambiente presencial, barulhento, o celular fica no centro da mesa
  ou na mão. Uso em rajadas curtas (marca e volta a jogar).
- **Plataforma:** Android nativo (Kotlin, Jetpack Compose, Material 3). Offline,
  sem login, sem backend, sem anúncios.
- **Orientação:** retrato é prioridade; precisa funcionar bem em **tablets** também.

### Regras que o placar cobre
- Partida vai até **12 tentos**.
- Uma mão vale **1** (normal), **3** (truco), **6** (seis), **9** (nove), **12** (doze).
- O app agora trabalha com **série**: melhor de N — vence a série a primeira dupla
  a ganhar **1, 2 ou 3 partidas** (escolhido ao criar). Cada partida vai a 12.

---

## 2. Princípios de UX inegociáveis

1. **Marcar um tento em menos de 1 segundo.** Sem menu para a ação principal.
2. **Números enormes.** O placar é o elemento mais importante da tela, de longe.
3. **Uma mão só.** Áreas de toque grandes e confortáveis, alcançáveis com o polegar.
4. **Alto contraste e legível de longe** (o celular pode estar a 40–60 cm, na mesa).
5. **Poucos textos na tela principal.**
6. **Nunca depender só de cor** para transmitir informação (acessibilidade / TalkBack).
7. Animações **rápidas e discretas** — nunca atrapalham a marcação.
8. Erro é comum (toca no botão errado) → **desfazer** sempre acessível.

---

## 3. Identidade visual atual

Direção: **mesa de carteado elegante, porém moderna.** Verde feltro, vermelho,
dourado e branco.

### Paleta (tokens atuais)

| Nome            | Hex        | Uso                                   |
|-----------------|------------|---------------------------------------|
| FeltGreenDeep   | `#07271D`  | fundo (tema escuro)                   |
| FeltGreenDark   | `#0B3B2E`  | superfície / cards (tema escuro)      |
| FeltGreen       | `#12503D`  | surfaceVariant                        |
| FeltGreenLight  | `#1C6A50`  | outline / bordas                      |
| FeltGreenPale   | `#DCEDE4`  | fundo (tema claro)                    |
| TrucoRed        | `#C0392B`  | dupla adversária (tema claro)         |
| TrucoRedBright  | `#E74C3C`  | dupla adversária (tema escuro) / erro |
| TrucoRedDark    | `#7B241C`  | container vermelho                    |
| Gold            | `#D4AF37`  | dupla "nossa" / marca / destaque      |
| GoldBright      | `#F0D27B`  | dourado claro                         |
| GoldDark        | `#8A6D1F`  | dourado no tema claro                 |
| OffWhite        | `#F5F1E6`  | texto sobre verde                     |
| InkGreen        | `#0A2A1F`  | texto sobre dourado / tema claro      |

### Mapeamento Material 3 (para a resposta mapear de volta ao código)

**Tema escuro** (fica muito bom): `primary` = Gold, `onPrimary` = InkGreen,
`secondary` = TrucoRedBright, `background` = FeltGreenDeep, `surface` = FeltGreenDark,
`surfaceVariant` = FeltGreen, `onBackground`/`onSurface` = OffWhite,
`outline` = FeltGreenLight, `error` = TrucoRedBright.

**Tema claro** (hoje fica pálido/lavado): `primary` = GoldDark, `onPrimary` = OffWhite,
`secondary` = TrucoRed, `background` = FeltGreenPale, `surface` = branco puro,
`onSurface` = InkGreen, `surfaceVariant` = `#CFE3D8`, `outline` = FeltGreenLight.

**Cor por dupla:** dupla 1 ("Nossa Dupla") usa o **dourado** (`primary`); dupla 2
("Adversários") usa o **vermelho** (`secondary`). Isso é consistente em todo o app.

### Tipografia
- 100% **fonte de sistema** (sans-serif). Sem personalidade própria.
- `displayLarge`/score em **peso Black**. Número do placar: ~**96–104 sp**.
- Marca "TENTO": 68 sp Black na tela inicial; menor no cabeçalho do placar.
- `titleLarge`, `labelLarge`, `headlineMedium` em **bold**.

### Forma
- Cards da dupla: cantos **24 dp**. Botões: **16–18 dp**. Barra de progresso: 4 dp.

### Ícone do app
- Adaptive icon. Fundo verde `#0B3B2E` com um quadrado de borda sutil.
- Primeiro plano: **marcador de tentos clássico** — 4 riscos verticais dourados
  cruzados por 1 risco diagonal vermelho.

---

## 4. Telas e componentes atuais

### 4.1 Tela inicial (Start)
Centralizada, vertical:
```
                    ⚙ (canto sup. direito)
                   TENTO           (68sp, dourado, Black)
              Placar de Truco
     "Rápido, bonito e feito para marcar com uma mão só."

     [  ▶ Continuar série  (1 x 0)  ]   ← só se há série em andamento
       Série até 3 partidas · partida 2  (1 x 6)
     [        Nova série / Nova partida        ]   ← contorno
     [            Como jogar                   ]   ← contorno
          Regras do Truco (Paulista, Goiano e Mineiro)   ← link
```
- Quando não há nada em andamento: só um botão preenchido grande **"Nova partida"**.

### 4.2 Nova partida (setup)
Scaffold com "← Nova partida":
- Campo "Nome da sua dupla" (default "Nossa Dupla")
- Campo "Nome dos adversários" (default "Adversários")
- "Partidas para vencer a série" → **segmented control**: `Única · 2 · 3` (default 3)
- Texto de apoio explicando o formato
- Nota: "Variante: Truco Paulista, Goiano ou Mineiro — o placar é o mesmo."
- Botão preenchido **"Começar série" / "Começar partida"**

### 4.3 Placar (Scoreboard) — TELA PRINCIPAL, a mais importante

Layout atual (retrato), tudo numa `Column` rolável:
```
←  TENTO                                        ⋮
   Série até 3 partidas
┌───────────────────────┐   ┌───────────────────────┐
│  Nossa Dupla      ✎    │   │  Adversários     ✎    │
│  PARTIDAS 1/3  ● ○ ○   │   │  PARTIDAS 0/3  ○ ○ ○  │
│                       │   │                       │
│          1            │   │          6            │   ← ~100sp Black
│                       │   │                       │
│  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁     │   │  ▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁     │   ← barra progresso
│      1 / 12 tentos    │   │      6 / 12 tentos    │
│                       │   │                       │
│   (−)     [   +   ]    │   │   (−)     [   +   ]    │   ← + grande = accent
│  [  TRUCO  ·  +3  ]    │   │  [  TRUCO  ·  +3  ]    │   ← contorno accent
│  "Toque no número     │   │  "Toque no número     │   ← helper pequeno
│   para +6, +9 ou +12" │   │   para +6, +9 ou +12" │
└───────────────────────┘   └───────────────────────┘
     Série 1 x 0 · partida 2 — Adversários na frente: 6 / 12
┌─────────────────────────────────────────────────────┐
│  Histórico                              1p · 3  ⌄    │
│  ↺ Desfazer                                         │
│  (expandível: "Partidas da série" + "Lançamentos    │
│   da partida atual")                                │
└─────────────────────────────────────────────────────┘
[            ↻  ZERAR SÉRIE / ZERAR PARTIDA           ]   ← contorno
```

Interações:
- **`+`** grande = +1. **`TRUCO · +3`** = +3. **`−`** = −1 (nunca < 0).
- **Tocar no número** (ou segurar o `+`) abre um *bottom sheet* "Adicionar tentos"
  com `+1 +3 +6 +9 +12`. Se o valor passa de 12, pede confirmação (limita a 12).
- **Editar nome:** ✎ ao lado do nome → diálogo com campo de texto.
- Ao bater 12 numa partida → diálogo (ver 4.5), a partida vai pro histórico e a
  próxima começa 0×0.
- Série decidida → destaque de vitória (hoje: só borda dourada + diálogo).

### 4.4 Histórico (card na tela do placar)
Recolhido: título "Histórico", contador (`1p · 3` = 1 partida concluída, 3
lançamentos), botão **Desfazer**. Expandido: lista "Partidas da série"
(`P1  Nossa Dupla   12 × 0`) e "Lançamentos da partida atual" (`3. Adversários +3`).

### 4.5 Diálogos (AlertDialog Material 3)
- **Partida encerrada (série continua):** "Partida 1 encerrada / Nossa Dupla venceu
  12 x 0 / Série: 1 x 0 — próxima é a partida 2" → `Próxima partida` · `Desfazer partida`
- **Fim da série:** "🏆 Fim da série / Nossa Dupla venceu a série! / Partidas: 2 x 0 /
  Última partida: 12 x 0" → `Nova série` · `Ver placar`
- **Fim de jogo (partida única):** "🏆 Fim de jogo / X venceu! / 12 x 8" → `Nova partida` · `Continuar`
- **Zerar série/partida:** confirmação destrutiva.
- **Editar nome da dupla:** campo de texto + contador de caracteres.
- **Adicionar tentos** (ModalBottomSheet): botões tonais `+1 +3 +6 +9 +12` num FlowRow.

### 4.6 Regras
Scaffold rolável, cards ("InfoCard": título dourado + parágrafos):
`Antes de tudo` · `Comum às três variantes` · `Truco Paulista` · `Truco Mineiro`
· `Truco Goiano`. Bastante texto.

### 4.7 Como jogar
Cards: `O essencial` (tabela Mão/valor: 1/3/6/9/12), `Série (melhor de N)`,
`Marcando no app`. Link "Ver regras completas".

### 4.8 Configurações
Cards: `Tema` (segmented Sistema/Claro/Escuro), `Feedback` (switches Vibração,
Sons), `Dados` (botão vermelho "Resetar dados"), `Sobre` (nome, versão, descrição).

### Componentes reutilizáveis
- **TeamPanel** — o card da dupla (nome+✎, PARTIDAS+bolinhas, número gigante,
  progresso, `−`/`+`, `TRUCO`, helper).
- **BigAddButton** — retângulo `accent` cheio, ícone `+`, long-press abre o sheet.
- **TrucoButton** — contorno + fundo `accent @ 16%`, texto `TRUCO · +3`.
- **GamesWonRow** — "PARTIDAS n/N" + N bolinhas (cheias = vitórias).
- **Barra de progresso** até 12 + texto "n / 12 tentos".
- **Badge flutuante "+N"** — aparece ~900 ms no canto superior direito do número
  quando soma tentos (animação de escala/slide).
- **InfoCard** — card de conteúdo (Regras / Como jogar / Configurações).
- **TentoScaffold** — top app bar transparente com "← Título".

---

## 5. Estados que o design precisa cobrir

- Série nova, 0×0, partida 1 (nada no histórico).
- Meio de partida, uma dupla na frente.
- **Empate** (mesmo número de tentos).
- Perto do fim (11/12) — precisa de tensão visual sem susto.
- Partida encerrada (12) — diálogo por cima.
- **Série encerrada** — celebração + placar final, dupla vencedora destacada.
- Nome de dupla longo (até 24 caracteres).
- Partida única (`gamesToWin = 1`): esconder tudo de "série" (bolinhas, linha de
  série, textos) — vira um placar simples.
- Tema claro **e** escuro. Tablet (retrato e paisagem) **e** celular retrato.

---

## 6. O que já está bom / o que incomoda

**Bom, manter:**
- Direção "mesa de carteado" com dourado/vermelho/verde — principalmente no **tema escuro**.
- Número gigante + `+` grande cumprem o "marcar rápido".
- Botão `TRUCO · +3` dedicado (pedido recorrente do usuário).
- Ícone do marcador de tentos.

**Incomoda / quero melhorar:**
1. **Tema claro fica lavado** — cards brancos puros sobre verde pálido perdem a
   identidade de mesa de carteado. O escuro é muito melhor; o claro precisa de
   personalidade equivalente.
2. **TeamPanel está cheio** — nome+✎, PARTIDAS+bolinhas, número, barra+texto,
   `−`/`+`, `TRUCO`, helper. Sete blocos empilhados. Poderia respirar mais e ter
   hierarquia mais clara (número domina, resto recua).
3. **Densidade vertical da tela do placar** — cabeçalho + 2 painéis + linha de
   status + card de histórico + botão zerar → rola em telas menores. A ação
   principal deveria caber sempre sem rolar.
4. **Sem separador entre as duplas** — o "×" central do placar de truco sumiu; os
   dois cards só ficam lado a lado com um gap.
5. **Helper "Toque no número para +6, +9 ou +12"** é pequeno e fácil de ignorar;
   a descoberta do menu rápido é fraca.
6. **Celebração de vitória é fraca** — só borda dourada + AlertDialog padrão. O
   fim da série (o clímax do app) merece algo memorável, mas ainda discreto.
7. **Tipografia sem caráter** — tudo fonte de sistema. A marca "TENTO" e o número
   do placar poderiam ter uma voz tipográfica.
8. **Bottom sheet "Adicionar tentos"** é genérico (botões tonais num FlowRow).
9. **Bolinhas de "PARTIDAS"** — a diferença cheia/vazia é sutil (accent vs
   accent @ 22%). Tem o texto "n/N" junto, então não é só cor, mas dá pra deixar
   mais claro.
10. **Tablet / paisagem** — hoje só estica a largura; os painéis ficam enormes.
    Falta um layout pensado para telas largas.
11. **Telas de Regras** têm muito texto corrido; poderiam ser mais escaneáveis.

---

## 7. Restrições para a proposta

- **Implementável em Jetpack Compose + Material 3.** Descrever em termos de
  `ColorScheme` (roles M3), `Typography`, `Shapes`, componentes M3 e modifiers.
  Se sugerir fonte custom, indicar alternativa (o app hoje não embute fontes).
- **Não** quebrar os princípios da seção 2 (rapidez, número gigante, uma mão,
  contraste, não-só-cor, animação discreta).
- **Não** transformar em jogo de cartas / IA / multiplayer / cadastro.
- Manter **tema claro e escuro** e **acessibilidade** (TalkBack, alvos ≥ 48 dp,
  contraste AA no texto, foco visível).
- Manter a semântica de cor por dupla (dourado = dupla 1, vermelho = dupla 2) ou
  propor uma substituição melhor, justificando.
- Movimento: no máximo transições curtas (~150–300 ms), um "pulso" na mudança de
  número, e uma celebração de fim de série. Nada que atrase o toque seguinte.

---

## 8. O que eu quero de volta (formato da resposta)

Um `.md` com:

1. **Direção visual** — 1 recomendação principal + no máximo 1 alternativa. Humor,
   referência, o que muda em relação ao atual.
2. **Sistema** — paleta refinada (light + dark) mapeada nos roles do Material 3;
   escala tipográfica (com pesos e tamanhos, incluindo o número do placar e a
   marca); shapes; elevação/bordas; espaçamento base.
3. **Tela a tela** — para cada tela da seção 4, o layout proposto (wireframe em
   ASCII ou descrição), hierarquia, o que entra/sai/muda, e como fica em **tablet /
   paisagem** para a tela do placar.
4. **Componentes** — redesenho do **TeamPanel** (o mais importante), do
   `BigAddButton` + `TrucoButton`, das bolinhas de partidas, da barra de progresso,
   do badge "+N", do bottom sheet de tentos e dos diálogos de fim de partida/série.
5. **Movimento** — lista curta de animações (gatilho, o que anima, duração).
6. **Celebração de fim de série** — proposta concreta e contida.
7. **Prioridade** — o que dá mais retorno de design com menos esforço de código.

Pode assumir que a lógica está pronta e não muda — é só design/UI.
