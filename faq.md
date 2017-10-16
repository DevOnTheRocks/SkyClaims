# Frequently Asked Questions

## How do I prevent players from building outside their islands?

> ~~By default GriefPrevention allows players to build in the wilderness (unclaimed areas). To limit players from building outside their island, (claimed area) permission to `place-block`s must be denied in the wilderness using [GP flags](https://github.com/MinecraftPortCentral/GriefPrevention/wiki/Flags).~~ **Update to Beta 23+ to have these flags set automatically.**

> To manually set the flag, stand/fly in the wilderness _(verify claim type using /claiminfo)_ and run the command `/cf block-break any false`.

## How do I create a void world?
> SkyClaims does not affect world generation in any way. You must use Sponge, another plugin or a mod to control world generation. Sponge offers the `sponge:void` world generation modifier that can be used in the world.conf Sponge config of that world or with world management plugins such as Nucleus or Project Worlds. The Forge mods YUNoMakeGoodMap or Garden of Glass may also be used to configure void generation.

