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

Our plugin has 5 shop types, Each type got its own implementation.

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

`bucket:` Will be the form name. in most cases you would want this to set it to the itemname.

`permission:` Can be set on PLAYER or ADMIN. When permission is set on admin the player does not have the permission; bedrockformshop.adminmenu 
the button will not be visable to that player. 

`image:` This option can be set on 3 values.<br />
    DEFAULT -> "default" Which will render the java 3D image from the item, in this case a bucket.<br />
    URL -> "https://google.com/myepicimage.png" Images can be loaded from an external url. <br />
    PATH -> "textures/blocks/dirt.png" This is the path option, this will render images from the bedrock vanilla cient which are 2D <br />
         
`type:` ITEM, type item will treat item as an item. Type currently has 5 options; item, enchantment, command, potion and spawners.
in case you want to buy or sell a normal item the type has to be set on ITEM.
you can also enchant normal items "tools and armour" to add enchantments see; ENCHANTMENT type example.

`item:` Is the material name, see https://www.digminecraft.com/lists/item_id_list_pc_1_9.php for a full list of item names. Be aware that item names has to be uppercase!

`buy-price:` Buy price per item.<br />
`sell-price:` Sell price per item. when this option is 0.0 players wont be able to sell this item!


