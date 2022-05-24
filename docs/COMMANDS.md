# Commands
Commands are separated into two groups, the first one being user commands, and the second being admin commands. Commands may not be issued unless the player has the permission node or have op.

##User Commands
#### Usage "/pmine (subcommand) [subcommand args]"
- User Commands and their required permission nodes.
  - Add [player] - **PlayerMines.Player.Add**
    - Add a player to your private mine.
  - Contents [block] (percent) - **PlayerMines.Player.Contents**
    - Set a block to the percentage specified. Replaces entire mine contents if no percent is specified.
  - Create - **PlayerMines.Player.Create**
    - Creates the players private mine.
  - Private - **PlayerMines.Player.Private**
    - Sets your mine to private or public.
  - Remove [player] - **PlayerMines.Player.Remove**
    - Remove a player from your private mine. Does nothing to public mine.
  - Reset - **PlayerMines.Player.Reset**
    - Resets the command senders private mine.
  - Tp [username] - **PlayerMines.Player.Teleport**
      - Teleports the command sender to their private mine or another players private mine.
  - Upgrade [(Upgrade)](UPGRADES.md) [level] - **PlayerMines.Player.Upgrade**
      - Upgrades a specific PlayerMine Upgrade by one level or to the specified level. 
##Admin Commands
#### Usage "/pmine admin (subcommand) [subcommand args]"
- Admin Commands and their required permission nodes.
    - Config [config] [option] [args] - **PlayerMines.Admin.Config**
        - Useful config features ingame. **NOT SET UP CURRENTLY**
    - Delete [username] - **PlayerMines.Admin.Delete**
      - Delete the specified users mine
    - SpawnMC (username) - **PlayerMines.Admin.SpawnMC**
      - Spawn a MineCrate on the top layer at your mine or the specified players mine.
    - TP (username) - **PlayerMines.Admin.TP**
      - Teleport to your mine or the specified players mine without the restricting border.