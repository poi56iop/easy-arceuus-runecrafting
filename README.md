# Easy Arceuus Runecrafting
Contextual highlights and screen flashing for Arceuus Runecrafting.

Soul runes are not very well implemented at the moment. (You won't see ground highlights to tell you to walk to/from the soul altar.)

Low agility levels are also not very well implemented. (If you stop halfway to a shortcut when going to/from essence rocks, it might not re-highlight the shortcut.)

There's a chance you'll need to walk a little bit until a chunk loads before the plugin will activate - if you logged in already within the essence mine or dark/blood/soul altar areas.

To make things simple, this plugin assumes you'll do things in a relatively reasonable way, and might give bad advice otherwise. For example:
- If your inventory has both fragments and dark essence blocks and is not full, you will be told to chisel, even if you've reached a max stack of fragments. (I'd love to fix this, but can't figure out how to query the quantity of fragments held.)
- If you use all your fragments on an altar, chisel all your dark essence blocks into fragments, and walk away from the blood/soul altars a bit, it'll direct you to mine more essence rather than click the altar again.