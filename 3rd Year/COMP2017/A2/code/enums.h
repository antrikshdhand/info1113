#ifndef ENUMS_H
#define ENUMS_H

/* INSTRUCTION SET OPCODES */
enum Opcode
{
    // ADD, SUB, SLL, SLT, SLTU, XOR, SRL, SRA, OR, AND 
    OP_R_TYPE    = 0b0110011, 

    // BEQ, BNE, BLT, BLTU, BGE, BGEU
    OP_B_TYPE    = 0b1100011,

    // SB, SH, SW
    OP_S_TYPE    = 0b0100011,

    // LB, LH, LW, LBU, LHU
    OP_LOAD_TYPE = 0b0000011,

    // ADDI, XORI, ORI, ANDI, SLTI, SLTIU
    OP_IMM_TYPE  = 0b0010011,
    
    // OPERATIONS WITH UNIQUE OPCODES
    OP_LUI       = 0b0110111,
    OP_JAL       = 0b1101111,
    OP_JALR      = 0b1100111,
};

enum BTypeFunct3
{
    BEQ     = 0b000,
    BNE     = 0b001,
    BLT     = 0b100,
    BGE     = 0b101,
    BLTU    = 0b110,
    BGEU    = 0b111
};

enum LoadTypeFunct3
{
    LB      = 0b000,
    LH      = 0b001,
    LW      = 0b010,
    LBU     = 0b100,
    LHU     = 0b101
};

enum STypeFunct3
{
    SB = 0b000,
    SH = 0b001,
    SW = 0b010
};

enum ImmTypeFunct3
{
    ADDI    = 0b000,
    SLTI    = 0b010,
    SLTIU   = 0b011,
    XORI    = 0b100,
    ORI     = 0b110,
    ANDI    = 0b111
};

enum RTypeFunct3
{
    ADD_SUB     = 0b000,
    SLL         = 0b001,
    SLT         = 0b010,
    SLTU        = 0b011,
    XOR         = 0b100,
    SRL_SRA     = 0b101,
    OR          = 0b110,
    AND         = 0b111
};

enum RTypeFunct7
{
    FUNCT_7_ON = 0b0100000,
    FUNCT_7_OFF = 0b0000000
};

enum VirtualRoutines
{
    CONSOLE_WRITE_CHAR = 0x0800,
    CONSOLE_WRITE_INT = 0x0804,
    CONSOLE_WRITE_UINT = 0x0808,
    HALT= 0x080C,
    CONSOLE_READ_CHAR = 0x0812,
    CONSOLE_READ_INT = 0x0816,
    DUMP_PC = 0x0820,
    DUMP_REGISTER_BANKS = 0x0824,
    DUMP_MEMORY_WORD = 0x0828,
    MALLOC = 0x0830,
    FREE = 0x0834
};

#endif