# Tento — Proposta de Melhoria de Design

> Resposta do **Claude Design** ao [briefing](DESIGN_BRIEF.md). Serviu de base para
> a 1ª rodada de melhorias visuais (ver seção "Design" no [README](../README.md)).
> Nem tudo foi implementado — itens adiados estão marcados no README.

---

## 1. Direção visual

**Recomendação principal: "Mesa de baeta, à noite" — o escuro atual como o padrão
de verdade, e um tema claro que é a mesma mesa sob luz de dia, não uma versão
"clareada" dela.**

O problema do tema claro não era a paleta, era a ausência de superfície própria:
cards brancos puros sobre verde pálido leem como Material Design genérico com cor
de destaque trocada. A correção é dar às superfícies do tema claro uma cor —
nunca branco puro — e reintroduzir um sinal físico de "mesa" (borda/friso) que
só existia no escuro.

O que muda:

- Card da dupla no claro passa de branco puro para um "marfim" quente
  (`#F7F3E8`), com friso superior de 3dp na cor da dupla (dourado ou vermelho).
- Humor "chip de cassino / mesa de jogo": formas em pílula nos botões de ação,
  círculos sólidos nas bolinhas de partida, numerais com peso e caráter.
- Acabamento contemporâneo: sombra levemente colorida na cor da dupla, gradiente
  sutil de profundidade nos cards, ícones sempre desenhados (nunca glifos de
  texto como ✎ ← ⋮ −).

**Alternativa: "Tabuleiro de placar"** — fundo quase preto-verde com números em
blocos separados por linhas douradas finas (como um scoreboard mecânico), cantos
quase retos, alto contraste tipo LED. Mais dramático, mais distante do Material
padrão, maior risco de parecer "frio". Não é a recomendação principal.

---

## 2. Sistema

### 2.1 Paleta — Material 3 ColorScheme

**Tema escuro** (mantém a base, ajustada aos roles M3 completos):
`background #07271D` · `surface #0B3B2E` · `surfaceContainerHigh #123D30` ·
`surfaceVariant #12503D` · `onSurface #F5F1E6` · `outline #1C6A50` ·
`primary #D4AF37` (dupla 1) · `secondary #E74C3C` (dupla 2).

Nota: `error` e `secondary` colidiam (vermelho servia para "dupla 2" e para
"erro"). Proposta: `error` num vermelho dessaturado e mais escuro (`#B33B2C`
escuro / `#8A3226` claro), reservado só para "Zerar" e confirmações destrutivas.

**Tema claro** (a correção central):
`background #D8E8DE` · `surface #F7F3E8` marfim (substitui branco puro) ·
`surfaceContainerHigh #FBF8EF` · `onSurface #0A2A1F` · `primary #7A5F16`
(dourado escuro, AA no marfim).

Cor por dupla: **dourado** (dupla 1) e **vermelho** (dupla 2). Como o dourado
vivo não passa contraste como texto no marfim, separa-se em três papéis:
`accent` (preenchimento vivo), `onAccent` (sobre o accent), `onCard` (cor de
marca como texto sobre o card — escura no claro).

### 2.2 Tipografia

Manter fonte de sistema para toda a UI funcional; introduzir **uma** fonte de
exibição só para "TENTO" e o número do placar. Recomendação: **Bricolage
Grotesque** (OFL). Alternativa sem fonte nova: **algarismos tabulares**
(`FontFeature.TabularNums`), que resolve o "salto" de largura de 1 → 11.

### 2.3 Formas, elevação, espaçamento

- TeamPanel 24dp, canto superior um pouco maior que o inferior.
- Botões `+` e `TRUCO` em **pílula**.
- Sombra tintada na cor da dupla (não sombra cinza genérica).
- Reduzir padding interno do TeamPanel para ganhar altura de tela.

### 2.4 Ícone

Os 4 riscos + 1 diagonal do marcador de tentos sobre um cartão/pergaminho
levemente inclinado, com sombra própria — para ser lido como "marcação de
pontos" e não como forma geométrica.

---

## 3. Tela a tela (resumo)

- **Tela inicial:** "TENTO" na fonte de exibição; textura radial sutil no fundo.
- **Nova partida:** sem mudança de layout.
- **Placar (a tela que recebe a reformulação):**
  - Separador **"×" central** entre os dois cards.
  - `PARTIDAS` + bolinhas colapsam numa linha; bolinha cheia ganha um **"check"**.
  - Helper "+6 · +9 · +12" vira **legenda única** sob os dois cards.
  - **Botão "Zerar" sai da tela** e vai para o menu `⋮`.
  - Alvo: caber sem rolar em telefones compactos.
  - **Tablet / paisagem:** layout de **3 colunas** (dupla · coluna central com
    ×/histórico/desfazer · dupla), número em tamanho compacto.
- **Histórico:** sem mudança estrutural.
- **Regras:** tabs por variante em vez de cards empilhados; parágrafos longos
  viram listas curtas.

---

## 4. Componentes

- **TeamPanel:** de 7 blocos empilhados para **3 níveis de peso** — número domina,
  nome+partidas identificam, controles recuam.
- **BigAddButton / TrucoButton:** pílula, gradiente leve, sombra tintada, ícone
  desenhado; ripple de escala no toque.
- **GamesWonRow:** traço "check" na bolinha cheia (`Canvas.drawLine`).
- **Barra de progresso:** mais fina, com 12 marcações.
- **Badge "+N":** pílula na cor da dupla.
- **Bottom sheet "Adicionar tentos":** 5 pílulas na cor da dupla, com rótulos
  "truco" / "seis".
- **Diálogos de fim:** área de título com fundo na cor da dupla vencedora e o
  placar em tamanho grande; ícone Material no lugar do emoji 🏆.

---

## 5. Movimento

| Gatilho | O que anima | Duração |
|---|---|---|
| Toque em + / − | escala do botão 1.0→0.94→1.0 | 80ms |
| Número muda | "pulso" 1.0→1.06→1.0 | 150ms |
| Tento adicionado | badge "+N" sobe e some | 900ms |
| Barra de progresso | largura até o novo valor | 200ms |
| Placar ≥ 11/12 | glow do número (sem loop) | 250ms |
| Troca de tela | sharedAxis / fade | ~300ms |
| Fim de série | ver seção 6 | ~1.6s, não bloqueia toque |

---

## 6. Celebração de fim de série

Contida: borda da tela recebe um brilho pulsante único na cor da dupla vencedora
(~1.6s) atrás do diálogo; o placar final aparece no tamanho "de jogo" (o mesmo do
número principal); o troféu entra com um overshoot leve. Sem som/vibração
obrigatórios além dos configuráveis. Custo baixo, nenhum asset novo.

---

## 7. Prioridade (retorno de design ÷ esforço de código)

**Alto retorno, baixo esforço:** paleta do tema claro · separador "×" · botões em
pílula · "check" na bolinha · Zerar no menu `⋮`.

**Alto retorno, esforço médio:** hierarquia do TeamPanel · layout de 3 colunas
para paisagem/tablet · bottom sheet redesenhado.

**Depois:** tipografia de exibição · tabs na tela de Regras · celebração de fim
de série.
