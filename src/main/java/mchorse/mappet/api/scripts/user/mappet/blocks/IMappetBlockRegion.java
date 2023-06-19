package mchorse.mappet.api.scripts.user.mappet.blocks;

import mchorse.mappet.api.scripts.code.mappet.blocks.MappetBlockRegion;
import mchorse.mappet.api.scripts.code.mappet.conditions.MappetCondition;
import mchorse.mappet.api.scripts.code.mappet.triggers.MappetTrigger;
import mchorse.mappet.api.scripts.user.IScriptWorld;

/**
 * Represents the interface for a programmatically manipulated region block.
 *
 * <pre>{@code
 * function main(c)
 * {
 *     var regionBlock = mappet.createRegionBlock()
 *         .setPassable(true)
 *         .setCheckEntities(true)
 *         .setUpdateFrequency(1)
 *
 *         .addCylinderShape(0.75, 0.5, 0, 1, 0);
 *
 *     var regionBlockTrigger = regionBlock.getOnEnterTrigger()
 *         .addScriptBlock()
 *         .setInlineCode(function() {
 *             c.getSubject().send("Welcome to this region!");
 *         });
 *
 *     regionBlock.place(c.getWorld(), 0, 4, 0);
 * }
 * }</pre>
 */
public interface IMappetBlockRegion
{
    /**
     * Notifies all client about all changes.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * //do something with the region block...
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @return this region block instance
     */
    MappetBlockRegion notifyUpdate();

    /**
     * Places the region block at the given coordinates.
     *
     * <pre>{@code
     * var regionBlock = mappet.createRegionBlock();
     * //do something with the region block...
     * regionBlock.place(c.getWorld(), 0, 4, 0);
     * }</pre>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     *
     * @return this region block instance
     */
    IMappetBlockRegion place(IScriptWorld world, int x, int y, int z);

    /**
     * Sets whether the region block is passable.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.setPassable(true);
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @param isPassable whether the region block is passable
     *
     * @return this region block instance
     */
    IMappetBlockRegion setPassable(boolean isPassable);

    /**
     * Sets whether the region block should check entities.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.setCheckEntities(true);
     * regionBlock.notifyUpdate();
     * }</pre>
     * @param checkEntities whether the region block should check entities
     *
     * @return this region block instance
     */
    IMappetBlockRegion setCheckEntities(boolean checkEntities);

    /**
     * Sets the delay of the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.setDelay(20);
     * regionBlock.notifyUpdate();
     * }</pre>
     * @param delay the delay
     * @return this region block instance
     */
    IMappetBlockRegion setDelay(int delay);

    /**
     * Sets the update frequency of the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.setUpdateFrequency(1);
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @param frequency the update frequency
     *
     * @return this region block instance
     */
    IMappetBlockRegion setUpdateFrequency(int frequency);

    /**
     * Adds a box shape to the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.addBoxShape(1, 1, 1, 0, 0, 0);
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @param offsetZ the z offset
     * @param halfSizeX half the size of the x axis
     * @param halfSizeY half the size of the y axis
     * @param halfSizeZ half the size of the z axis
     * @param offsetX the x offset
     * @param offsetY the y offset
     *
     * @return this region block instance
     */
    IMappetBlockRegion addBoxShape(double halfSizeX, double halfSizeY, double halfSizeZ, double offsetZ, double offsetX, double offsetY);

    /**
     * Adds a box shape to the region block without an offset.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.addBoxShape(1, 1, 1);
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @param halfSizeX half the size of the x axis
     * @param halfSizeY half the size of the y axis
     * @param halfSizeZ half the size of the z axis
     *
     * @return this region block instance
     */
    IMappetBlockRegion addBoxShape(double halfSizeX, double halfSizeY, double halfSizeZ);

    /**
     * Adds a sphere shape to the region block without an offset.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.addSphereShape(1, 1);
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @param horizontalRadius the horizontal radius of the sphere
     * @param verticalRadius the vertical radius of the sphere
     *
     * @return this region block instance
     */
    IMappetBlockRegion addSphereShape(double horizontalRadius, double verticalRadius);

    /**
     * Adds a sphere shape to the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.addSphereShape(1, 1, 0, 0, 0);
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @param horizontalRadius the horizontal radius of the sphere
     * @param verticalRadius the vertical radius of the sphere
     * @param offsetZ the z offset
     * @param offsetX the x offset
     * @param offsetY the y offset
     *
     * @return this region block instance
     */
    IMappetBlockRegion addSphereShape(double horizontalRadius, double verticalRadius, double offsetZ, double offsetX, double offsetY);

    /**
     * Adds a cylinder shape to the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.addCylinderShape(1, 1, 0, 0, 0);
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param offsetZ the z offset
     * @param offsetX the x offset
     * @param offsetY the y offset
     *
     * @return this region block instance
     */
    IMappetBlockRegion addCylinderShape(double radius, double height, double offsetZ, double offsetX, double offsetY);

    /**
     * Adds a cylinder shape to the region block without an offset.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.addCylinderShape(1, 1);
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     *
     * @return this region block instance
     */
    IMappetBlockRegion addCylinderShape(double radius, double height);

    /**
     * Clears all shapes from the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * regionBlock.clearShapes();
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @return this region block instance
     */
    MappetBlockRegion clearShapes();

    /**
     * Gets on enter trigger of the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * var regionBlockTrigger = regionBlock.getOnEnterTrigger();
     * var scriptTriggerBlock = regionBlockTrigger.addScriptBlock();
     * scriptTriggerBlock.setInlineCode(function() {
     *     c.getSubject().send("You entered the region!");
     * });
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @return the on enter trigger
     */
    MappetTrigger getOnEnterTrigger();

    /**
     * Gets on exit trigger of the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * var regionBlockTrigger = regionBlock.getOnExitTrigger();
     * var scriptTriggerBlock = regionBlockTrigger.addScriptBlock();
     * scriptTriggerBlock.setInlineCode(function() {
     *     c.getSubject().send("You left the region!");
     * });
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @return the on exit trigger
     */
    MappetTrigger getOnExitTrigger();

    /**
     * Gets on tick trigger of the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     * var regionBlockTrigger = regionBlock.getOnTickTrigger();
     * var scriptTriggerBlock = regionBlockTrigger.addScriptBlock();
     * scriptTriggerBlock.setInlineCode(function() {
     *     c.getSubject().send("You are in the region!");
     * });
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @return the on tick trigger
     */
    MappetTrigger getOnTickTrigger();

    /**
     * Gets the condition of the region block.
     *
     * <pre>{@code
     * var regionBlock = c.getWorld().getRegionBlock(0, 4, 0);
     *
     * regionBlock.getCondition().addStateBlock()
     * .setTargetMode("global")
     * .setStateKey("can_pass")
     * .setComparator("==")
     * .setComparisonValue(1);
     *
     * regionBlock.notifyUpdate();
     * }</pre>
     *
     * @return region block's condition instance
     */
    MappetCondition getCondition();
}