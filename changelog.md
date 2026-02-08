* Add new restriction config options for the following modifications:
  * **Invisible** - (The ability to make an armor stand invisible)
  * **BasePlate** - (The ability to remove the base plate of an armor stand)
  * **Gravity** - (The ability to disable gravity for an armor stand)
  * **ShowArms** - (The ability to show arms on an armor stand)
  * **Small** - (The ability to make an armor stand small)
  * **NameVisible** - (The ability to make the name of an armor stand visible)
  * **Rotation** - (The ability to set the rotation of an armor stand)
  * **Align** - (The ability to use the align block/item/tool options for an armor stand)
  * **Resize** - (The ability to resize an armor stand)
* Add `restrictWhitelist` config option 
  * List of players allowed to bypass enabled restrictions. Entries can be either a username to bypass all restrictions (e.g. "shynieke") or a specific restriction using the format "username,feature" (e.g. "shynieke,resize").