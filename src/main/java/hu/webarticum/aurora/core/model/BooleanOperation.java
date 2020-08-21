package hu.webarticum.aurora.core.model;

import java.io.Serializable;

public enum BooleanOperation implements Serializable {

    // 0000
    FALSE {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return false;
        }

    },

    // 1000
    NOR {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return !(operand1 || operand2);
        }

    },

    // 0100
    XRIGHT {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return (!operand1 && operand2);
        }

    },

    // 0010
    XLEFT {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return (operand1 && !operand2);
        }

    },

    // 0001
    AND {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return (operand1 && operand2);
        }

    },

    // 1100
    NLEFT {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return !operand1;
        }

    },

    // 1010
    NRIGHT {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return !operand2;
        }

    },

    // 1001
    NXOR {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return (operand1 == operand2);
        }

    },

    // 0110
    XOR {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return (operand1 != operand2);
        }

    },

    // 0101
    RIGHT {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return operand2;
        }

    },

    // 0011
    LEFT {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return operand1;
        }

    },

    // 1110
    NAND {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return !(operand1 && operand2);
        }

    },

    // 1101
    NXLEFT {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return (!operand1 || operand2);
        }

    },

    // 1011
    NXRIGHT {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return (operand1 || !operand2);
        }

    },

    // 0111
    OR {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return (operand1 || operand2);
        }

    },

    // 1111
    TRUE {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean operate(boolean operand1, boolean operand2) {
            return true;
        }

    },
    
    ;
    
    public abstract boolean operate(boolean operand1, boolean operand2);
    
}
