package org.adamnew123456.source2html.syntax.parsing;

/**
 * This is used for strong checkpoints. See CheckpointStream for more details.
 */
public interface StrongCheckpoint {
    boolean needsRestore();
}
