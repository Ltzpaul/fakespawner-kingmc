package vn.kingsmp.fakespawner;

import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;

/**
 * Luu trang thai cua mot Long trieu hoi Skeleton gia (thuc chat la Khoi Dong tren server).
 */
public class FakeSpawnerData {
    public final BlockPos pos;
    public int stackCount;
    public int currentExp;
    public int maxExpK;
    public int boneCount;
    public final List<SlotRecord> consumedSlots = new ArrayList<>();

    public record SlotRecord(int slotIndex, int count) {}

    public FakeSpawnerData(BlockPos pos, int initialCount, int slotIndex, int hiddenCount) {
        this.pos = pos.toImmutable();
        this.stackCount = Math.max(1, initialCount);
        if (hiddenCount > 0) {
            this.consumedSlots.add(new SlotRecord(slotIndex, hiddenCount));
        }
        recalculateStats(true);
    }

    public void addStack(int addedCount, int slotIndex, int hiddenCount) {
        this.stackCount += addedCount;
        if (hiddenCount > 0) {
            this.consumedSlots.add(new SlotRecord(slotIndex, hiddenCount));
        }
        recalculateStats(true);
    }

    public void recalculateStats(boolean refillExp) {
        // Theo video: 256 Skeleton -> DANG CO: 512/768K EXP va 8 stack 64 Xuong (512 xuong)
        this.maxExpK = this.stackCount * 3;
        if (refillExp) {
            this.currentExp = this.stackCount * 2;
        }
        this.boneCount = this.stackCount * 2;
    }

    public int getMinDrop() {
        return this.stackCount * 2;
    }

    public int getMaxDrop() {
        return this.stackCount * 4;
    }

    public int getMinExp() {
        return this.stackCount;
    }

    public int getMaxExp() {
        return this.stackCount * 5;
    }
}
