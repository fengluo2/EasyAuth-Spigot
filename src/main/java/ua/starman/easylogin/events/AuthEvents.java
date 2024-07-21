package ua.starman.easylogin.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ua.starman.easylogin.auther.AuthWorker;
import ua.starman.easylogin.auther.PlayerData;
import ua.starman.easylogin.limits.IpLimit;
import ua.starman.easylogin.limits.TimeLimit;
import ua.starman.easylogin.utils.Utils;
import ua.starman.easylogin.utils.Vars;

import java.time.Duration;
import java.time.LocalDateTime;

public class AuthEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (PlayerData.check(player.getUniqueId())) {
            PlayerData playerData = PlayerData.get(player.getUniqueId());

            if (IpLimit.working && !playerData.ip.equals(AuthWorker.getIPAddress(player))) {
                Vars.plugin.getLogger().info(String.format("Player %s was blocked", player.getName()));
                player.setMetadata("auth_block", new FixedMetadataValue(Vars.plugin, true));

                player.sendMessage(Utils.parseMessage(ChatColor.RED + "You joined from different IP address. Use /login <password>"));
            } else if (TimeLimit.working && Duration.between(playerData.lastLogin, LocalDateTime.now()).toMinutes() >= TimeLimit.time) {
                Vars.plugin.getLogger().info(String.format("Player %s was blocked", player.getName()));
                player.setMetadata("auth_block", new FixedMetadataValue(Vars.plugin, true));

                player.sendMessage(Utils.parseMessage(ChatColor.RED + "Use /login <password>"));
            }
        } else {
            player.setMetadata("auth_block", new FixedMetadataValue(Vars.plugin, true));
            player.setMetadata("auth_register", new FixedMetadataValue(Vars.plugin, true));

            player.sendMessage(Utils.parseMessage(ChatColor.RED + "Hello! Before joining to our server you need to register! Use /register <password> <password>"));
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        AuthWorker.loginCancel(player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        AuthWorker.loginCancel(player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        AuthWorker.loginCancel(player, event);
    }
}
