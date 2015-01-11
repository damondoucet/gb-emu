package memory;

/**
 * Handles writes to the ROM, generally for switching ROM/RAM banks during
 * execution.
 *
 * For the sake of equality and toString, we act as if implementors are stateless.
 * This is because the CartridgeHeader's equality check only cares which type
 * of MemoryBankController was found, not the state that the MBC is manipulating.
 */
public interface MemoryBankController {
    // TODO(ddoucet): Add methods here as needed
}
