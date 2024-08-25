package magmaout.mappet.api.scripts.code;

import magmaout.mappet.api.scripts.user.entities.IScriptEntity;
import magmaout.mappet.api.scripts.code.entities.ScriptEntity;
import magmaout.mappet.api.scripts.user.IScriptRayTrace;
import magmaout.mappet.api.scripts.user.data.ScriptVector;
import net.minecraft.util.math.RayTraceResult;

public class ScriptRayTrace implements IScriptRayTrace {
    private RayTraceResult result;
    private IScriptEntity entity;

    public ScriptRayTrace(RayTraceResult result) {
        this.result = result;
    }

    @Override
    public RayTraceResult getMinecraftRayTraceResult() {
        return this.result;
    }

    @Override
    public boolean isMissed() {
        return this.result.typeOfHit == RayTraceResult.Type.MISS;
    }

    @Override
    public boolean isBlock() {
        return this.result.typeOfHit == RayTraceResult.Type.BLOCK;
    }

    @Override
    public boolean isEntity() {
        return this.result.typeOfHit == RayTraceResult.Type.ENTITY;
    }

    @Override
    public IScriptEntity getEntity() {
        if (this.result.entityHit == null) {
            return null;
        }

        if (this.entity == null) {
            this.entity = ScriptEntity.create(this.result.entityHit);
        }

        return this.entity;
    }

    @Override
    public ScriptVector getBlock() {
        return new ScriptVector(this.result.getBlockPos());
    }

    @Override
    public ScriptVector getHitPosition() {
        return new ScriptVector(this.result.hitVec);
    }
}