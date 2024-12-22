package ua.starman.easylogin.auther;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.metadata.MetadataValue;
import ua.starman.easylogin.utils.Utils;
import ua.starman.easylogin.utils.Vars;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

public class AuthWorker {
    public static boolean checkPassword(String password, PlayerData playerData) {
        String true_pass;

        if (Objects.equals(Vars.encode, "SHA256")) {
            true_pass = Utils.encodeToSHA256(password);
        } else {
            true_pass = password;
        }

        return playerData.password.equals(true_pass);
    }

    public static void loginCancel(Player player, Event event) {
        List<MetadataValue> metadataBlockedList = player.getMetadata("auth_block");
        for (MetadataValue metadataBlocked : metadataBlockedList) {
            if (metadataBlocked.asBoolean()) {
                if (event instanceof Cancellable) {
                    ((Cancellable) event).setCancelled(true);
                }
            }
            List<MetadataValue> metadataNeedRegisterList = player.getMetadata("auth_register");

            if (metadataNeedRegisterList.isEmpty() && metadataBlocked.asBoolean()) {
                player.sendMessage(Utils.parseMessage(ChatColor.RED + "Enter /login <password>"));
            } else if (!metadataNeedRegisterList.isEmpty() && metadataBlocked.asBoolean()) {
                if (Vars.register) {
                    player.sendMessage(Utils.parseMessage(ChatColor.RED + "Enter /register <password> <password>"));
                } else {
                    player.sendMessage(Utils.parseMessage(ChatColor.RED + "Server is closed register function"));
                }
            }

        }
    }

    public static InetAddress getIPAddress(Player player) {
        try {
            return InetAddress.getByName(Objects.requireNonNull(player.getAddress()).getHostString());
        } catch (UnknownHostException e) {

            return null;
        }
    }
}
