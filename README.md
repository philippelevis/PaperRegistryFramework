what it says on the tin!
adds a hotswappable json-based loottable system, a generic tickable class and a generic entity-tied effect class.

the loottable system consists of two systems: one provides a table that outputs one item based on weights in the json (SingleLootTable) and the other provides a table that may output multiple items based on weights in the json.
by default, the tables are stored in plugins/random-paper-framework/loot_tables, to add one, create a json describing it.

the recipe system is similar, but currently only supports shaped recipes. the format for it is :
a list of objects. each object must have a "ingredients" list and a "result" string. the list consists of objects which have a field with the ID of your item set to the amount of items in the given slot. if the slot must be empty use minecraft:air (e.g. {"minecraft:air": 1}). the result is a string with the ID of the item you intend to be the result of the craft.

the tickable class, as well as the effect class are mostly used to make something happen every tick. tickable is a runnable that executes every tick, effect is a tickable that is tied to an entity and has a length (e.g. an effect may stop after one minute).
As it is implemented, to make the new effect track itself properly, you are required to change the EffectName field in your constructor to a unique string. best practice is to use your plugin namespace with the effect name. If you do not change the field, your effect will not be able to load after a relog and will notify you in console. 

**Important Note:**
**I do not intend to work on this project. I tried working with Paper/Spigot and its derivatives and I found myself unable to bear the lack of certain tools i am used to on fabric. If you want to continue work on this, feel free to fork or make a PR.**
