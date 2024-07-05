package ua.starman.easylogin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import ua.starman.easylogin.auther.AuthWorker;
import ua.starman.easylogin.auther.PlayerData;
import ua.starman.easylogin.utils.Utils;
import ua.starman.easylogin.utils.Vars;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            List<MetadataValue> metadataBlockedList = ((Player) sender).getMetadata("auth_block");

            if (metadataBlockedList.isEmpty()) {
                sender.sendMessage(Utils.parseMessage(ChatColor.GREEN + "You are already authed!"));
                return true;
            }

            for (MetadataValue metadataBlocked : metadataBlockedList) {
                if (metadataBlocked.asBoolean()) {
                    List<MetadataValue> metadataNeedRegisterList = ((Player) sender)
                            .getMetadata("auth_register");
                    for (MetadataValue metadataNeedRegister : metadataNeedRegisterList) {
                        if (metadataNeedRegister.asBoolean()) {
                            sender.sendMessage(Utils.parseMessage(ChatColor.RED + "You need to register!"));
                            return false;
                        }
                    }

                    if (args.length > 0) {
                        PlayerData playerData = PlayerData.get(((Player) sender).getUniqueId());

                        if (AuthWorker.checkPassword(args[0], playerData)) {

                            ((Player) sender).setMetadata("auth_block",
                                    new FixedMetadataValue(Vars.plugin, false));

                            playerData.lastLogin = LocalDateTime.now();
                            playerData.ip = AuthWorker.getIPAddress(Objects.requireNonNull(((Player) sender).getPlayer()));

                            playerData.save();

                            sender.sendMessage(Utils.parseMessage(ChatColor.GREEN + "Have a nice game!"));
                        } else {
                            sender.sendMessage(Utils.parseMessage(ChatColor.RED + "Wrong password!"));
                        }

                    } else  {
                        sender.sendMessage(Utils.parseMessage(ChatColor.RED + "Password not passed!"));
                    }
                } else {
                    sender.sendMessage(Utils.parseMessage(ChatColor.GREEN + "You are already authed!"));
                }
            }

        }
        return false;
    }
}
