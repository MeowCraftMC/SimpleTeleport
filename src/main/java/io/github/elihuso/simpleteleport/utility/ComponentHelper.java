package io.github.elihuso.simpleteleport.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class ComponentHelper {

    private static Component createPlayerReference(Player player) {
        return player.displayName()
                .hoverEvent(HoverEvent.showEntity(player.getType().key(), player.getUniqueId()))
                .color(NamedTextColor.RED);
    }

    private static Component createCommand(String command) {
        return Component.text(command)
                .decoration(TextDecoration.ITALIC, true)
                .hoverEvent(HoverEvent.showText(Component.text("点击直接运行此命令")))
                .clickEvent(ClickEvent.runCommand(command))
                .color(NamedTextColor.RED);
    }

    private static Component createJoin(Component prefix, Component suffix, Component joiner) {
        return Component.empty()
                .append(prefix)
                .append(joiner)
                .append(suffix);
    }

    private static Component createJoin(String prefix, String suffix, Component joiner) {
        return createJoin(Component.text(prefix + " "), Component.text(" " + suffix), joiner);
    }

    public static Component createCommandNotPlayer() {
        return Component.text("请以玩家身份运行此命令！").color(NamedTextColor.RED);
    }

    public static Component createNoBackPoint() {
        return Component.text("还没有记录到可以返回的位置！").color(NamedTextColor.RED);
    }

    public static Component createBackPreferenceSet() {
        return Component.text("保存成功。").color(NamedTextColor.GREEN);
    }

    public static Component createTeleportFailed() {
        return Component.text("由于 Bukkit API 的限制导致传送失败了！").color(NamedTextColor.RED);
    }

    public static Component createBackSuccessful() {
        return Component.text("Wooah~").color(NamedTextColor.GREEN);
    }

    public static Component createPlayerOffline() {
        return Component.text("玩家不在线！").color(NamedTextColor.RED);
    }

    public static Component createTpaToSelf() {
        return Component.text("你不能向自己发送传送请求！").color(NamedTextColor.RED);
    }

    public static Component createTpaRequestExists() {
        return Component.text("你已经向对方发送了一个请求，请等待对方处理，不要重复发送！").color(NamedTextColor.RED);
    }

    public static Component createTpaRequesterResult(Player target, long timeout) {
        return Component.empty()
                .append(createJoin("传送到", "的请求发送成功：", createPlayerReference(target)).color(NamedTextColor.GREEN).append(Component.newline()))
                .append(createJoin("请求在", "秒内有效，", Component.text(timeout).color(NamedTextColor.RED)).color(NamedTextColor.GOLD).append(Component.newline()))
                .append(createJoin("使用", "以取消。", createCommand("/tpcancel " + target.getName())).color(NamedTextColor.GOLD));
    }

    public static Component createTpaRequestedMessage(Player requester, long timeout) {
        return Component.empty()
                .append(createJoin("玩家", "想要传送到你的位置：", createPlayerReference(requester)).color(NamedTextColor.GREEN).append(Component.newline()))
                .append(createJoin("使用", "以接受；", createCommand("/tpaccept " + requester.getName())).color(NamedTextColor.GOLD).append(Component.newline()))
                .append(createJoin("使用", "以拒绝。", createCommand("/tpdeny " + requester.getName())).color(NamedTextColor.GOLD).append(Component.newline()))
                .append(createJoin("请求在", "秒内有效。", Component.text(timeout).color(NamedTextColor.RED)).color(NamedTextColor.GOLD));
    }

    public static Component createTpHereRequesterResult(Player target, long timeout) {
        return Component.empty()
                .append(createJoin("传送", "到你的位置的请求发送成功：", createPlayerReference(target)).color(NamedTextColor.GREEN).append(Component.newline()))
                .append(createJoin("请求在", "秒内有效，", Component.text(timeout).color(NamedTextColor.RED)).color(NamedTextColor.GOLD).append(Component.newline()))
                .append(createJoin("使用", "以取消。", createCommand("/tpcancel " + target.getName())).color(NamedTextColor.GOLD));
    }

    public static Component createTpHereRequestedMessage(Player requester, long timeout) {
        return Component.empty()
                .append(createJoin("玩家", "想要你传送到对方的位置：", createPlayerReference(requester)).color(NamedTextColor.GREEN).append(Component.newline()))
                .append(createJoin("使用", "以接受；", createCommand("/tpaccept " + requester.getName())).color(NamedTextColor.GOLD).append(Component.newline()))
                .append(createJoin("使用", "以拒绝。", createCommand("/tpdeny " + requester.getName())).color(NamedTextColor.GOLD).append(Component.newline()))
                .append(createJoin("请求在", "秒内有效。", Component.text(timeout).color(NamedTextColor.RED)).color(NamedTextColor.GOLD));
    }

    public static Component createTpNoSuchRequest(Player player) {
        return createJoin("没有关于", "的传送请求可以处理！", createPlayerReference(player)).color(NamedTextColor.RED);
    }

    public static Component createTpCanceledTo(Player player) {
        return createJoin("已取消向", "发送的传送请求。", createPlayerReference(player)).color(NamedTextColor.GREEN);
    }

    public static Component createTpCanceledAll() {
        return Component.text("已取消所有未被处理的传送请求。").color(NamedTextColor.GREEN);
    }

    public static Component createTpCanceled(Player player) {
        return createJoin("玩家", "发起的传送请求已取消。", createPlayerReference(player)).color(NamedTextColor.GOLD);
    }

    public static Component createTpAcceptedFrom(Player player) {
        return createJoin("已接受来自", "的传送请求。", createPlayerReference(player)).color(NamedTextColor.GREEN);
    }

    public static Component createTpAcceptedAll() {
        return Component.text("已接受所有传送请求。").color(NamedTextColor.GREEN);
    }

    public static Component createTpAcceptedBy(Player player) {
        return createJoin("你的传送请求已经被", "接受。", createPlayerReference(player)).color(NamedTextColor.GOLD);
    }

    public static Component createTpDeniedFrom(Player player) {
        return createJoin("已拒绝来自", "的传送请求。", createPlayerReference(player)).color(NamedTextColor.GREEN);
    }

    public static Component createTpDeniedAll() {
        return Component.text("已拒绝所有传送请求。").color(NamedTextColor.GREEN);
    }

    public static Component createTpDeniedBy(Player player) {
        return createJoin("你的传送请求已经被", "拒绝。", createPlayerReference(player)).color(NamedTextColor.GOLD);
    }
}
