package magmaout.mappet.api.expressions;

import magmaout.mappet.api.expressions.functions.State;
import magmaout.mappet.api.expressions.functions.entity.EntityFunction;
import magmaout.mappet.api.expressions.functions.entity.PlayerIsAlive;
import magmaout.mappet.api.expressions.functions.inventory.InventoryArmor;
import magmaout.mappet.api.expressions.functions.inventory.InventoryHas;
import magmaout.mappet.api.expressions.functions.inventory.InventoryHolds;
import magmaout.mappet.api.expressions.functions.world.WorldIsDay;
import magmaout.mappet.api.expressions.functions.world.WorldIsNight;
import magmaout.mappet.api.expressions.functions.world.WorldTime;
import magmaout.mappet.api.expressions.functions.world.WorldTotalTime;
import magmaout.mappet.api.utils.DataContext;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Variable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.Map;

public class ExpressionManager {
    public static IValue ONE = new Constant(1);
    public static IValue ZERO = new Constant(0);

    public MathBuilder builder;
    public DataContext context;

    public ExpressionManager() {
        this.builder = new MathBuilder().lenient();

        /* Functions */

        this.builder.functions.put("state", State.class);

        this.builder.functions.put("inv_has", InventoryHas.class);
        this.builder.functions.put("inv_holds", InventoryHolds.class);
        this.builder.functions.put("inv_armor", InventoryArmor.class);

        this.builder.functions.put("entity", EntityFunction.class);
        this.builder.functions.put("player_is_alive", PlayerIsAlive.class);

        this.builder.functions.put("world_time", WorldTime.class);
        this.builder.functions.put("world_total_time", WorldTotalTime.class);
        this.builder.functions.put("world_is_day", WorldIsDay.class);
        this.builder.functions.put("world_is_night", WorldIsNight.class);
    }

    private void reset() {
        for (Map.Entry<String, Variable> entry : this.builder.variables.entrySet()) {
            String key = entry.getKey();
            Variable variable = entry.getValue();

            if (key.equals("PI") || key.equals("E")) {
                continue;
            }

            if (variable.isNumber()) {
                variable.set(0);
            } else {
                variable.set("");
            }
        }

        this.context = null;
    }

    public ExpressionManager set(World world) {
        return this.set(new DataContext(world));
    }

    public ExpressionManager set(EntityLivingBase subject) {
        return this.set(new DataContext(subject));
    }

    public ExpressionManager set(DataContext context) {
        this.reset();

        this.context = context;

        for (Map.Entry<String, Object> entry : context.getValues().entrySet()) {
            String key = entry.getKey();
            Variable variable = this.builder.variables.get(key);

            if (variable == null) {
                variable = new Variable(key, 0);
                this.builder.register(variable);
            }

            if (entry.getValue() instanceof Number) {
                variable.set(((Number) entry.getValue()).doubleValue());
            } else if (entry.getValue() instanceof String) {
                variable.set((String) entry.getValue());
            }
        }

        return this;
    }

    public IValue parse(String expression) {
        return this.parse(expression, ZERO);
    }

    public IValue parse(String expression, IValue defaultValue) {
        try {
            return this.builder.parse(expression);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    /* External API */

    public World getWorld() {
        return this.context.world;
    }

    public MinecraftServer getServer() {
        return this.context.getSender().getServer();
    }
}