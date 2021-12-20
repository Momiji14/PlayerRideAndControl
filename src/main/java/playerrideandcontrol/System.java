package playerrideandcontrol;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

public final class System extends JavaPlugin implements Listener {

    static double TargetMaxDistance;
    static double ThrowPower;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reload();
    }

    @Override
    public void onDisable() {
        reloadConfig();
        saveConfig();
    }

    void reload() {
        reloadConfig();
        TargetMaxDistance = getConfig().getDouble("TargetMaxDistance", 16);
        ThrowPower = getConfig().getDouble("ThrowPower", 2);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("ride")) {
                RayTraceResult ray = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), TargetMaxDistance, entity -> entity != player);
                if (ray != null && ray.getHitEntity() != null) {
                    Entity target = ray.getHitEntity();
                    target.addPassenger(player);
                }
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("playerRideAndControlReload")) {
            reload();
            sender.sendMessage("Config Reloaded");
            return true;
        }
        return false;
    }

    @EventHandler
    void onDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        if (attacker.getPassengers().contains(victim)) {
            victim.eject();
            Vector vector = attacker.getLocation().getDirection().multiply(ThrowPower);
            victim.setVelocity(vector);
        } else {
            attacker.sendMessage("test");
        }
    }
}
