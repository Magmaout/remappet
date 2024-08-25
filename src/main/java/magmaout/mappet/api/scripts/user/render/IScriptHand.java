package magmaout.mappet.api.scripts.user.render;

import magmaout.mappet.api.scripts.user.data.ScriptVector;

public interface IScriptHand {

    void setSkinPath(String path);

    void setSkinType(String type);

    String getSkinPath();

    String getSkinType();

    /**
     * Resets the render, rotations and position of the hand.
     */
    void resetAll();

    /**
     * Rotates the arm (main or off). angle - angle in degrees (positive angle = counterclockwise rotation), and x, y and z - vector around which the rotation is performed.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        const hand = c.player.getHand(0); //0 - main, 1 - off
     *
     *        hand.setRotate(0, 0, 45); // Rotate 45 degrees clockwise around the vertical axis
     *    }
     *  }</pre>
     */
    void setRotations(double x, double y, double z);

    /**
     * Return rotate arm.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        const rotate = c.player.getHand(0).getRotate(); //0 - main, 1 - off
     *
     *        c.send(rotate.x); // returns x arm
     *        c.send(rotate.y); // returns y arm
     *        c.send(rotate.z); // returns z arm
     *    }
     *  }</pre>
     */
    ScriptVector getRotations();

    /**
     * Moves the arm through the coordinates
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        const hand = c.player.getHand(0); //0 - main, 1 - off
     *
     *        hand.setPosition(0.3, 0, 0);
     *    }
     *  }</pre>
     */
    void setPosition(double x, double y, double z);

    /**
     * Return coordinates arm
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        const hand = c.player.getHand(0); //0 - main, 1 - off
     *        const position = hand.getPosition();
     *
     *        c.send("x: "+position.x+", y:"+position.y+", z:"+position.z)
     *    }
     *  }</pre>
     *  /**
     */
    ScriptVector getPosition();

    /**
     * Returns whether hand is render
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        const hand = c.player.getHand(0); //0 - main, 1 - off
     *
     *        c.send(hand.isRender()) // render: boolean
     *    }
     *  }</pre>
     */
    boolean isRender();

    /**
     * Disable arm render
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        const hand = c.player.getHand(0); //0 - main, 1 - off
     *
     *        hand.setRender(false)
     *    }
     *  }</pre>
     */
    void setRender(boolean render);

    void resetAllTo(String interpolation, int duration);

    void rotateTo(String interpolation, int duration, double x, double y, double z);

    void moveTo(String interpolation, int duration, double x, double y, double z);
}