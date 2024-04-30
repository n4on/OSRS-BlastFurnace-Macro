package net.runelite.client.plugins.microbot.BlastFurnaceNeon;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.BlastFurnaceNeon.Enums.states;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class BlastFurnaceNeonScript extends Script {
    public static double version = 1.0;
    public static states state;
    public static String barName;
    public static String barMaterialOne;
    public static String barMaterialTwo;
    public static String barMaterialThree;
    public static int barLevelRequired;
    public static int cofferCoins;
    public static WorldPoint point;
    public static int stamina;
    public static String materialToCollect;
    public static boolean coalCheck;

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    public String timestamp = LocalDateTime.now().format(formatter);

    public boolean run(BlastFurnaceNeonConfig config) {
        state = config.botState();
        barName = config.barType().getName();
        barMaterialOne = config.barType().getMaterialOne();
        barMaterialTwo = config.barType().getMaterialTwo();
        barMaterialThree = config.barType().getMaterialThree();
        barLevelRequired = config.barType().getLevelRequired();
        cofferCoins = config.CofferCoins();
        materialToCollect = config.barType().getMaterialOne();
        stamina = Microbot.getClient().getEnergy();
        coalCheck = false;

        point = new WorldPoint(1940, 4962, 0);

        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (!Microbot.hasLevel(barLevelRequired, Skill.SMITHING)) {
                    Microbot.showMessage("Smiting level to low to make " + config.barType().getName());
                    shutdown();
                    return;
                }

                switch (state) {
                    case checking:
                        checking();
                        break;
                    case coffer:
                        coffer();
                        break;
                    case foreman:
                        foreman();
                        break;
                    case bank:
                        bank();
                        break;
                    case materialOne:
                        materialOne();
                        break;
                    case materialTwo:
                        materialTwo();
                        break;
                    case materialThree:
                        materialThree();
                        break;
                    case gettingBars:
                        gettingBars();
                        break;
                    case materialCollect:
                        materialCollect(materialToCollect);
                        break;
                    case getCoal:
                        getCoal();
                        break;
                    case depositCoal:
                        depositCoal();
                        break;
                    case preCoal:
                        preCoal();
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                //System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
    }

    public void checking() {
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Checking \u001B[0m");

        if (Rs2Bank.isOpen()) {
            if (Rs2Inventory.hasItem(barName)) {
                Rs2Bank.depositAll(barName);
            }
            if (!Rs2Inventory.hasItem("Coins")) {
                Rs2Bank.withdrawAll("Coins");
            }
        } else if (!Rs2Bank.isOpen()) {
            Rs2Bank.useBank();
        }

        if (Microbot.getClient().getRealSkillLevel(Skill.SMITHING) < 60) {
            if (BlastFurnaceNeonPlugin.foremanTimerNeon == null) {
                System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Paying Foreman \u001B[0m");
                foreman();
            } else if (BlastFurnaceNeonPlugin.foremanTimerNeon.getEndTime().minusSeconds(80).getEpochSecond() <= System.currentTimeMillis() / 1000L && Rs2Inventory.hasItem("Coins")) {
                System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Paying Foreman \u001B[0m");
                foreman();
            }

            if (Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COFFER) < 2_000 && Rs2Inventory.hasItem("Coins")) {
                System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Filling Coffer with - " + cofferCoins + " Coins." + " \u001B[0m");
                coffer();
            }

            if (Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COFFER) > 2_000 && BlastFurnaceNeonPlugin.foremanTimerNeon.getEndTime().minusSeconds(80).getEpochSecond() > System.currentTimeMillis() / 1000L) {
                state = states.bank;
            }
        } else {
            if (Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COFFER) < 2_000 && Rs2Inventory.hasItem("Coins")) {
                System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Filling Coffer with - " + cofferCoins + " Coins." + " \u001B[0m");
                coffer();
            }

            if (Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COFFER) > 2_000) {
                if (barMaterialTwo.equals("Coal") && !coalCheck) {
                    state = states.preCoal;
                } else if (!barMaterialTwo.equals("Coal")) {
                    state = states.bank;
                } else {
                    state = states.bank;
                }
            }
        }
    }
    public void preCoal() {
        state = states.doing;
        coalCheck = true;
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Pre coal run \u001B[0m");

        if (Rs2Inventory.hasItem("Coins")) {
            Rs2Bank.depositAll("Coins");

            sleepUntil(()-> !Rs2Inventory.hasItem("Coins"), 2_000);
        }
        if (Rs2Inventory.hasItem("Stamina potion")) {
            Rs2Bank.depositAll("Stamina potion");

            sleepUntil(()-> !Rs2Inventory.hasItem("Stamina potion"), 2_000);
        }
        if (Rs2Inventory.hasItem("Vial")) {
            Rs2Bank.depositAll("Vial");

            sleepUntil(()-> !Rs2Inventory.hasItem("Vial"), 2_000);
        }


        Rs2Bank.withdrawAll("Coal");

        sleepUntil(()-> Rs2Inventory.hasItem("Coal"), 5_000);

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Pre coal run - Interacting \u001B[0m");
        Rs2GameObject.interact(9100);

        sleepUntil(()-> !Rs2Inventory.hasItem("Coal"), 7_000);

        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Pre coal run - Running to bank \u001B[0m");
        Rs2Bank.useBank();

        sleepUntil(Rs2Bank::isOpen);

        state = states.checking;
    }
    public void coffer() {
        state = states.doing;

        if (!Rs2Inventory.hasItem("Coins")) {
            Rs2Bank.useBank();
            sleepUntil(()-> Rs2Widget.hasWidget("The Bank of Gielinor"), 2_000);
            Rs2Bank.withdrawAll("Coins");
            sleepUntil(()-> Rs2Inventory.hasItem("Coins"), 2_000);
        }

        Rs2GameObject.interact("Coffer");

        sleepUntil(()-> Rs2Widget.hasWidget("Deposit coins."), 2_000);

        Microbot.doInvoke(new NewMenuEntry("Continue", "", 0, MenuAction.WIDGET_CONTINUE, 1, 14352385, false), Rs2Widget.getWidget(14352385).getBounds());

        sleepUntil(()-> Rs2Widget.hasWidget("Deposit how much?"), 2_000);

        VirtualKeyboard.typeString(Integer.toString(cofferCoins));
        VirtualKeyboard.enter();

        state = states.checking;
    }
    public void foreman() {
        state = states.doing;

        if (!Rs2Inventory.hasItem("Coins")) {
            Rs2Bank.useBank();
            sleepUntil(()-> Rs2Widget.hasWidget("The Bank of Gielinor"), 2_000);
            Rs2Bank.withdrawAll("Coins");
            sleepUntil(()-> Rs2Inventory.hasItem("Coins"), 2_000);
        }

        Rs2Npc.interact("Blast Furnace Foreman", "Pay");

        sleepUntil(()-> Rs2Widget.hasWidget("Pay 2,500 coins to use the Blast Furnace?"), 2_000);

        Microbot.doInvoke(new NewMenuEntry("Continue", "", 0, MenuAction.WIDGET_CONTINUE, 1, 14352385, false), Rs2Widget.getWidget(14352385).getBounds());

        state = states.checking;
    }
    public void bank() {
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] (2) Going to bank \u001B[0m");

        if (Rs2Bank.isOpen()) {
            if (Rs2Inventory.hasItem(barName)) {
                Rs2Bank.depositAll(barName);
            }

            if (Rs2Inventory.hasItem("Coins")) {
                Rs2Bank.depositAll("Coins");
            }

            stamina = Microbot.getClient().getEnergy();
            if (stamina <= 3000) {
                if (Rs2Inventory.isFull()) {
                    Rs2Bank.depositAll();
                }

                Rs2Bank.withdrawItem("Stamina potion");

                if (Rs2Inventory.hasItem("Stamina potion")) {
                    Rs2Inventory.interact("Stamina potion", "drink");
                }
            }

            if (Rs2Inventory.hasItem("Stamina potion")) {
                Rs2Player.toggleRunEnergy(true);
                Rs2Bank.depositAll("Stamina potion");
            }
            if (Rs2Inventory.hasItem("Vial")) {
                Rs2Bank.depositAll("Vial");
            }

            if (Rs2Inventory.hasItem(barMaterialOne) && stamina > 3000 && !Rs2Inventory.hasItem("Stamina potion") && !Rs2Inventory.hasItem("Vial")) {
                state = states.materialOne;
            } else if (!Rs2Inventory.hasItem(barMaterialOne) && stamina > 3000 && !Rs2Inventory.hasItem("Stamina potion") && !Rs2Inventory.hasItem("Vial")) {
                if (barMaterialTwo.equals("Coal")) {
                    state = states.getCoal;
                } else {
                    System.out.println("Withdrawing " + barMaterialOne);
                    Rs2Bank.withdrawAll(barMaterialOne);
                    sleep(600);
                }
            }
        } else if (!Rs2Bank.isOpen()) {
            Rs2Bank.useBank();
        }
    }
    public void getCoal() {
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Getting Coal & " + barMaterialOne + " \u001B[0m");

        if (Rs2Bank.isOpen()) {
            if (Rs2Inventory.hasItem(24480) || Rs2Inventory.hasItem(12019)) {
                if (Rs2Inventory.hasItem(453)) {
                    Rs2Inventory.combine(24480, 453);
                    sleep(600);
                    Rs2Bank.useBank();
                    sleep(600);
                    Rs2Bank.withdrawAll(barMaterialOne);
                    sleep(600);
                    state = states.materialOne;
                } else if (!Rs2Inventory.hasItem(453)) {
                    Rs2Bank.withdrawAll("Coal");
                }
            } else if (!Rs2Inventory.hasItem(24480)) {
                Rs2Bank.withdrawItem(24480);
            }
        } else if (!Rs2Bank.isOpen()) {
            Rs2Bank.useBank();
        }
    }
    public void materialCollect(String material) {
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Collecting " + material + " \u001B[0m");

        Rs2Bank.useBank();

        if (Rs2Bank.isOpen()) {
            Rs2Bank.withdrawAll(material);

            if (Rs2Inventory.hasItem(material) && material.equals(barMaterialTwo)) {
                state = states.materialTwo;
            }
        }
    }
    public void materialOne(){
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] (1) Depositing " + barMaterialOne + " \u001B[0m");

        Rs2GameObject.interact(9100);
        sleep(600);

        if (!Rs2Inventory.isFull() && barMaterialTwo.equals("NONE") && !Rs2Inventory.hasItem(barMaterialOne)) {
            state = states.gettingBars;
        }
        if (!Rs2Inventory.hasItem(barMaterialOne) && !barMaterialTwo.equals("NONE") && !barMaterialTwo.equals("Coal")) {
            System.out.println("(1) Inventory full = " + Rs2Inventory.isFull());
            materialToCollect = barMaterialTwo;
            state = states.materialCollect;
        }
        if (!Rs2Inventory.hasItem(barMaterialOne) && barMaterialTwo.equals("Coal")) {
            state = states.depositCoal;
        }
    }
    public void depositCoal() {
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Depositing Coal \u001B[0m");

        Rs2Inventory.interact("Open coal bag", "Empty");

        if (Rs2Inventory.hasItem("Coal")) {
            Rs2GameObject.interact(9100);
            sleep(600);

            state = states.gettingBars;
        }
    }
    public void materialTwo(){
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] (2) Depositing " + barMaterialTwo + " \u001B[0m");

        Rs2GameObject.interact(9100);
        sleep(600);

        if (!Rs2Inventory.isFull() && barMaterialThree.equals("NONE") && !Rs2Inventory.hasItem(barMaterialTwo)) {
            state = states.gettingBars;
        }
        if (!Rs2Inventory.hasItem(barMaterialTwo) && !barMaterialThree.equals("NONE")) {
            System.out.println("(2) Inventory full = " + Rs2Inventory.isFull());
            materialToCollect = barMaterialThree;
            state = states.materialCollect;
        }
    }
    public void materialThree(){
        state = states.doing;
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Depositing " + barMaterialThree + " \u001B[0m");

        Rs2GameObject.interact(9100);

        sleepUntil(()-> !Rs2Inventory.isFull(), 10_000);

        state = states.gettingBars;
    }
    public void gettingBars() {
        System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Collecting bars \u001B[0m");

        if (Rs2Player.getWorldLocation().distanceTo(point) > 2) {
            System.out.println("\u001B[95m[" + LocalDateTime.now().format(formatter) + "] Distance to position = " + Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(point) + "\u001B[0m");
            Microbot.getWalker().walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), point));
        }

        if (!Rs2Widget.hasWidget("How many would you like to take?") && Rs2Player.getWorldLocation().distanceTo(point) == 0 && !Rs2Inventory.hasItem(barName)) {
            Rs2GameObject.interact(9092);
        }

        if (Rs2Widget.hasWidget("How many would you like to take?")) {
            Microbot.doInvoke(new NewMenuEntry("Take", "<col=ff9040>Bronze bar</col>", 1, MenuAction.CC_OP, -1, 17694734, false), Rs2Widget.getWidget(17694734).getBounds());
        }

        if (Rs2Inventory.hasItem(barName)) {
            BlastFurnaceNeonPlugin.barCount += Rs2Inventory.count(barName);
            //state = states.bank;
            state = states.checking;
        }
    }
}
