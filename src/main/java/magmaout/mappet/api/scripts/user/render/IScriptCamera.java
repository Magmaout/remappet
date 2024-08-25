package magmaout.mappet.api.scripts.user.render;

import magmaout.mappet.api.scripts.user.data.ScriptVector;

/**
 * Represents an interface for controlling a script-based camera.
 */
public interface IScriptCamera {

    /**
     * Resets the rotations, position and scaling of the camera.
     */
    void resetAll();

    /**
     * Returns the current rotation angle and axis of the camera.
     *
     * @return a ScriptVector object containing the rotation axis vector
     */
    ScriptVector getRotations();

    /**
     * Sets the rotation of the camera around the specified axis.
     * Standart values: 0, 0, 0
     *
     * @param x the X component of the rotation axis vector
     * @param y the Y component of the rotation axis vector
     * @param z the Z component of the rotation axis vector
     */
    void setRotations(float x, float y, float z);

    /**
     * Returns the current scale of the camera.
     *
     * @return a ScriptVector object representing the scale of the camera
     */
    ScriptVector getScaling();

    /**
     * Sets the scale of the camera.
     * Standart values: 1, 1, 1
     *
     * @param x the X component of the scale vector
     * @param y the Y component of the scale vector
     * @param z the Z component of the scale vector
     */
    void setScaling(float x, float y, float z);

    /**
     * Returns the current position of the camera.
     *
     * @return a ScriptVector object representing the position of the camera
     */
    ScriptVector getPosition();

    /**
     * Sets the position of the camera.
     * Standart values: 0, 0, 0
     *
     * @param x the X component of the position vector
     * @param y the Y component of the position vector
     * @param z the Z component of the position vector
     */
    void setPosition(float x, float y, float z);

    void resetAllTo(String interpolation, int duration);
    void rotateTo(String interpolation, int duration, float x, float y, float z);
    void moveTo(String interpolation, int duration, float x, float y, float z);
    void scaleTo(String interpolation, int duration, float x, float y, float z);
}
