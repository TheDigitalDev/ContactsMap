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

    Craft tcraft; // Iterated craft
    private Craft ccraft; // Sender's craft

    @Override
    public void onEnable(){

    }

    @Override
    public void onDisable(){

    }

    public boolean onCommand(CommandSender cmdSender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("contactsmap")){

            if(args.length != 0) {
                if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("2")) {
                    generateContactsMap(cmdSender, Integer.valueOf(args[0]));
                    return true;
                }
            }
            generateContactsMap(cmdSender, 1);
            return true;
        }

        return true;
    }


    private void generateContactsMap(CommandSender cmdSender, int zoomSettings){

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
                      (int) getMidCoordinatesX(CraftManager.getInstance().getCraftByPlayer(p))
                    , (int) getMidCoordinatesZ(CraftManager.getInstance().getCraftByPlayer(p)))
                    , contactAirshipChunk, p, zoomSettings);

        }
    }

    private Chunk getAirshipChunk(Craft craft){
        return craft.getW().getChunkAt((int) getMidCoordinatesX(craft), (int) getMidCoordinatesZ(craft));
    }

    private void getPlayerContacts(Player p) {

        // This part is modified code of the contacts from CommandListener class(Line 306), written by BaccaYarro. Once again, NOT MY ORIGINAL CODE(This includes calc for getMidCoordinatesX/Z functions)

        Craft[] crafts;

        if (CraftManager.getInstance().getCraftsInWorld(p.getWorld()) != null) {

            ccraft = CraftManager.getInstance().getCraftByPlayer(p);

            crafts = CraftManager.getInstance().getCraftsInWorld(CraftManager.getInstance().getCraftByPlayer(p).getW());

            for (int i = 0; i < crafts.length; ++i) {
                Craft tcraft = crafts[i];
                if (tcraft != null) {


                    long cposx = (long)(ccraft.getMaxX() + ccraft.getMinX());
                    long cposy = (long)(ccraft.getMaxY() + ccraft.getMinY());
                    long cposz = (long)(ccraft.getMaxZ() + ccraft.getMinZ());
                    cposx >>= 1;
                    cposy >>= 1;
                    cposz >>= 1;
                    long tposx = (long)(tcraft.getMaxX() + tcraft.getMinX());
                    long tposy = (long)(tcraft.getMaxY() + tcraft.getMinY());
                    long tposz = (long)(tcraft.getMaxZ() + tcraft.getMinZ());
                    tposx >>= 1;
                    tposy >>= 1;
                    tposz >>= 1;
                    long diffx = cposx - tposx;
                    long diffy = cposy - tposy;
                    long diffz = cposz - tposz;
                    long distsquared = Math.abs(diffx) * Math.abs(diffx);
                    distsquared += Math.abs(diffy) * Math.abs(diffy);
                    distsquared += Math.abs(diffz) * Math.abs(diffz);
                    long detectionRange = 0L;


                    if(tposy > (long)tcraft.getW().getSeaLevel()) {
                        detectionRange = (long)(Math.sqrt((double)tcraft.getOrigBlockCount()) * tcraft.getType().getDetectionMultiplier());
                    } else {
                        detectionRange = (long)(Math.sqrt((double)tcraft.getOrigBlockCount()) * tcraft.getType().getUnderwaterDetectionMultiplier());
                    }


                    p.sendMessage("dr " + detectionRange * detectionRange + " distSq" + distsquared);
                    if(distsquared < detectionRange * detectionRange && tcraft.getNotificationPlayer() != ccraft.getNotificationPlayer()) {
                        p.sendMessage(tcraft.getNotificationPlayer() + " " + ccraft.getNotificationPlayer());
                        playerContacts.add(tcraft);
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
