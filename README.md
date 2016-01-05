# AutoErgel

AutoErgel is a crafting library for anything that supports a grid and is written in Java. You simply need to register your items and provide the crafting grid.

There are some implementations already provided. One of these is `AutoErgelCommon`, which provides common implementation utilities like [Mixin](https://github.com/SpongePowered/Mixin) for merging AutoErgel's interfaces with those likely provided by other crafting grids.
An actual implementation is `AutoErgelSponge`, which is targeted at the MC server API [SpongeAPI](https://github.com/SpongePowered/SpongeAPI).
