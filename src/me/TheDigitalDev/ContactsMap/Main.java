package me.TheDigitalDev.ContactsMap;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.lang.Math;

/*
 * Class created by TheDigitalDev on 5/5/2017.
 *
 * Plugin extension for BaccaYarro/Lorax42's Movecraft plugin
 *
 * TO ANYONE READING THIS CODE: THIS BUILD HAS NOT BEEN BUG TESTED! THERE ARE PROBABLY SEVERAL ISSUES!
 */

public class Main extends JavaPlugin{

    private final GenerateGrid gg = new GenerateGrid();
    private final List<Craft> playerContacts = new ArrayList<>();
    private final List<Chunk> contactAirshipChunk = new ArrayList<>();

    private long tpos;

    @Override
    public void onEnable(){

    }

    @Override
    public void onDisable(){

    }

    public boolean onCommand(CommandSender cmdSender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("contactsmap")){
            generateContactsMap(cmdSender);
            return true;
        }
        return false;
    }

    private void generateContactsMap(CommandSender cmdSender){

        // First let's get all the player's contacts through the method getPlayerContacts
        if(cmdSender != Bukkit.getConsoleSender() && CraftManager.getInstance().getCraftByPlayerName(cmdSender.getName()) != null) {

            playerContacts.clear();
            contactAirshipChunk.clear();

            Player p = Bukkit.getPlayer(cmdSender.getName());
            getPlayerContacts(p);

            // Get chunk of each airship from getPlayerContacts
            for(int i = 0; i < playerContacts.size(); i++){
                contactAirshipChunk.add(i, getAirshipChunk(playerContacts.get(i)));
            }

            // Messy code is my superpower!!!111!

            // Pass it onto generatemap, which will get the chunks, find their corner coordinates(modulus operator yay actual application) and use it to plot it on the message map
            gg.generatemap(CraftManager.getInstance().getCraftByPlayer(p).getW().getChunkAt(
                    (int) getMidCoordinatesX(CraftManager.getInstance().getCraftByPlayer(p)), (int) getMidCoordinatesZ(CraftManager.getInstance().getCraftByPlayer(p))),
                    contactAirshipChunk, p);

        }
    }

    private Chunk getAirshipChunk(Craft craft){
        return craft.getW().getChunkAt((int) getMidCoordinatesX(craft), (int) getMidCoordinatesZ(craft));
    }

    private void getPlayerContacts(Player p) {

        // This part is modified code of the contacts from CommandListener class(Line 306), written by BaccaYarro. Once again, NOT MY ORIGINAL CODE(This includes calc for getMidCoordinatesX/Z functions)

        int craftCount;

        if (CraftManager.getInstance().getCraftsInWorld(p.getWorld()) != null) {

            Craft[] crafts;

            crafts = CraftManager.getInstance().getCraftsInWorld(p.getWorld());
            craftCount = crafts.length;

            Craft tcraft = CraftManager.getInstance().getCraftByPlayer(p);
            for (int i = 0; i < craftCount; i++) {
                Craft craft = crafts[i];
                if (craft != null) {

                    long cposx = getMidCoordinatesX(craft);
                    long cposy = getMidCoordinatesY(craft);
                    long cposz = getMidCoordinatesZ(craft);

                    long tposx = getMidCoordinatesX(tcraft);
                    long tposy = getMidCoordinatesY(tcraft);
                    long tposz = getMidCoordinatesZ(tcraft);

                    long diffx = cposx - tposx;
                    long diffy = cposy - tposy;
                    long diffz = cposz - tposz;

                    long distsquared = Math.abs(diffx) * Math.abs(diffx);
                    distsquared += Math.abs(diffy) * Math.abs(diffy);
                    distsquared += Math.abs(diffz) * Math.abs(diffz);
                    long detectionRange;

                    if (tposy > 65L) {
                        detectionRange = (long) (Math.sqrt((double) tcraft.getOrigBlockCount()) * tcraft.getType().getDetectionMultiplier());
                    } else {
                        detectionRange = (long) (Math.sqrt((double) tcraft.getOrigBlockCount()) * tcraft.getType().getUnderwaterDetectionMultiplier());
                    }


                    if (distsquared < detectionRange * detectionRange && tcraft.getNotificationPlayer() != craft.getNotificationPlayer()) {
                        playerContacts.add(craft);
                    }

                }

            }
            if(playerContacts.size() == 0 ){
                p.sendMessage("No crafts found");
            }
        }
    }

    private long getMidCoordinatesX(Craft tcraft){

        tpos = (long) (tcraft.getMaxX() + tcraft.getMinX());
        tpos >>= 1;
        return tpos;

    }
    private long getMidCoordinatesY(Craft tcraft){

        tpos = (long) (tcraft.getMaxY() + tcraft.getMinY());
        tpos >>= 1;
        return tpos;
    }

    private long getMidCoordinatesZ(Craft tcraft){

        tpos = (long) (tcraft.getMaxZ() + tcraft.getMinZ());
        tpos >>= 1;
        return tpos;
    }
}
