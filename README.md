[![Discord](https://img.shields.io/discord/853331530004299807?color=7289da&label=discord&logo=discord&logoColor=white)](https://discord.gg/M2SvqCu4e9)

# BedrockFormShop
A shop plugin fully compatible with Bedrock Forms.

## Commands

| Commands | Permission |
| --- | --- |
| `/shop` | `bedrockformshop.openshop` |
| `/shop reload` | `bedrockformshop.reload` |

## Permissions

| info | Permission |
| --- | --- |
| `Display player-able buttons` | `bedrockformshop.playermenu` |
| `Display admin-able buttons` | `bedrockformshop.adminmenu` |

# Understanding the configuration.

### Common settings for all shop types
see ITEM type example for a more detailed explanation.

`bucket:` Will be the form name. in most cases you would want this to set it to the itemname.

`permission:` Can be set on PLAYER or ADMIN. When permission is set on admin the player does not have the permission; bedrockformshop.adminmenu
the button will not be visable to that player.

`image:` This option can be set on 3 values.<br />
DEFAULT -> "default" Which will render the java 3D image from the item, in this case a bucket.<br />
URL -> "https://google.com/myepicimage.png" Images can be loaded from an external url. <br />
PATH -> "textures/blocks/dirt.png" This is the path option, this will render images from the bedrock vanilla cient which are 2D <br />

`buy-price:` Buy price per item.<br />
`sell-price:` Sell price per item. when this option is 0.0 players wont be able to sell this item!

### ITEM type example:

```
      bucket:
        permission: PLAYER
        image: "default"
        type: ITEM
        item: "BUCKET"
        buy-price: 0.0
        sell-price: 0.0
```
 
`type:` ITEM, type item will treat item as an item. Type currently has 5 options; item, enchantment, command, potion and spawners.
in case you want to buy or sell a normal item the type has to be set on ITEM.
you can also enchant normal items "tools and armour" to add enchantments see; ENCHANTMENT type example.

`item:` Is the material name, see https://www.digminecraft.com/lists/item_id_list_pc_1_9.php for a full list of item names. Be aware that item names has to be uppercase!

### ENCHANTMENT type example:

```
      efficiency:
        permission: PLAYER
        image: "default"
        type: ENCHANTMENT
        item: "ENCHANTED_BOOK"
        enchantment: "EFFICIENCY:1"
        buy-price: 0.0
        sell-price: 0.0
```

`type:` ENCHANTMENT, type enchantment will apply an enchantment to an item or Enchanted Book.

`item:` You can add enchantments to every item that is able to set an enchantment to. Make sure that the item is in uppercase!

`enchantment:` All enchantments are supported, but not all enchantments can be applied to every block/item/tool see https://minecraft.fandom.com/wiki/Enchanting for a full list of enchantments and how to apply them.

### POTION type example:

```
      speed:
        permission: PLAYER
        image: "default"
        type: POTION
        item: "POTION"
        buy-price: 0.0
        sell-price: 0.0
        potion-data:
          type: "SPEED"
          splash: false
          extended: true
          upgraded: false
```

`potion-data:`<br />

&emsp;`type:` "SPEED" is the potion type, see https://minecraft.gamepedia.com/Potion_effect#Potion_types for a full list of potion types.<br />

&emsp;`splash:` false is the splash potion option, if set to true the potion will be a splash potion.<br />

&emsp;`extended:` true is the extended potion option, if set to true the potion will be a extended potion.<br />

&emsp;`upgraded:` false is the upgraded potion option, if set to true the potion will be a upgraded potion.<br />

### POTION type example:

```
      spawn:
        permission: PLAYER
        label: "You can buy the spawn command for $%buyprice% \nthe command will take you back to spawn"
        image: "https://i.imgur.com/XqQZQZq.png"
        type: COMMAND
        command: "spawn"
        buy-price: 0.0
```

`label:` You can add a custom text on the form.

`type:` COMMAND, type command will execute a command. Commands will be preformed as an OP'ed player and will bypass all permissions needed.

`command:` The command to preform. You can use the player placeholder %playername% to set the player name in command.