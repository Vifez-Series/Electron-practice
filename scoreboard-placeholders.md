# Scoreboard Placeholders

This document lists all placeholders you can use in your **scoreboard configuration** for Electron Practice.

---

## Global Placeholders (Available Everywhere)
| Placeholder         | Description                                         |
|--------------------|-----------------------------------------------------|
| `<username>`        | The player's username                               |
| `<global-elo>`      | Player's global ELO                                 |
| `<division>`        | Player's division (pretty name)                     |
| `<online>`          | Number of online players on the server             |
| `<playing>`         | Number of players currently in matches             |
| `<in-queue>`        | Number of players currently in queue               |
| `<ping>`            | Player's ping                                       |
| `<footer>`          | Footer text configured in `scoreboard.footer`      |
| `%animation%`       | Current frame of animated scoreboard title         |

---

## In-Lobby Placeholders
Used when the player is not in a match or queue.  
Template path: `scoreboard.in-lobby.lines`

| Placeholder | Description |
|-------------|-------------|
| `<username>` | Player's name |
| `<online>`   | Online players |
| `<playing>`  | Players in matches |
| `<in-queue>` | Players in queue |
| `<ping>`     | Player's ping |
| `<global-elo>` | Player's global ELO |
| `<division>`   | Player's division |
| `%animation%`  | Animated title frame |
| `<footer>`     | Footer text |

---

## In-Queue Placeholders
Used when the player is queued for a match.  
Template path: `scoreboard.in-queue.lines`

| Placeholder | Description |
|-------------|-------------|
| `<username>` | Player's name |
| `<kit>`      | Kit they are queued for (includes `[R]` for ranked) |
| `<time>`     | How long the player has been in queue |
| `<online>`   | Online players |
| `<in-queue>` | Players in queue |
| `<playing>`  | Players currently in matches |
| `<global-elo>` | Player's global ELO |
| `<division>`   | Player's division |
| `%animation%`  | Animated title frame |
| `<footer>`     | Footer text |

---

## In-Game Placeholders
Used when the player is in a match.

### Common Placeholders
| Placeholder       | Description |
|------------------|-------------|
| `<username>`      | Player's name |
| `<opponent>`      | Opponent's name |
| `<your-hits>`     | Player's hits in the match |
| `<their-hits>`    | Opponent's hits in the match |
| `<difference>`    | Hit difference between player and opponent (color-coded) |
| `<opponent-ping>` | Opponent's ping |
| `<duration>`      | Match duration |
| `<global-elo>`    | Player's global ELO |
| `<division>`      | Player's division |
| `%animation%`     | Animated title frame |
| `<footer>`        | Footer text |

### Match State Specific

#### STARTING

| Placeholder   | Description                         |
|---------------|-------------------------------------|
| `<starting-c>` | Countdown before match starts       |
| `<opponent>`   | Opponent's name                     |
| `<winner>`     | Winner (if any)                     |
| `<loser>`      | Loser (if any)                      |

#### STARTED

| Placeholder       | Description                         |
|------------------|-------------------------------------|
| `<your-hits>`     | Player's hits                        |
| `<their-hits>`    | Opponent's hits                      |
| `<difference>`    | Difference between hits (color-coded)|
| `<opponent>`      | Opponent's name                      |
| `<opponent-ping>` | Opponent's ping                      |
| `<duration>`      | Match duration                       |

#### ENDING

| Placeholder | Description         |
|-------------|-------------------|
| `<winner>`  | Winner's name      |
| `<loser>`   | Loser's name       |

---

## Notes
- All placeholders are **case-sensitive**.
- `%animation%` will update according to your configured animated frames.
- `<difference>` is color-coded based on whether the hit count is positive (`&a`), zero (`&e`), or negative (`&c`).

---

This allows you to fully customize your **scoreboard lines and titles** in `scoreboard.yml` using these placeholders.