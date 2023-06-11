#include <stdio.h>

#define PC_INCREMENT 0x00000004

#include "instructions.h"

// #define DEBUG 1 // debugging only

/* INCLUDES TRAP-AND-EMULATE FOR VIRTUAL ROUTINES */
void mem_trap_store(cpu_t* state, uint32_t addr, uint32_t data)
{
    // uint32_t heap_address;

    switch (addr)
    {
        case CONSOLE_WRITE_CHAR:
            // Console Write Character
            // Causes the virtual machine to print the value being stored as
            // a single ASCII encoded character to stdout.

            #ifdef DEBUG
            printf("You have entered the CONSOLE_WRITE_CHAR routine\n");
            #endif

            putchar((char) data & 0xFF); // Extract LSB as ASCII character

            break;

        case CONSOLE_WRITE_INT:
            // Console Write Signed Integer
            // Causes the virtual machine to print the value being stored as
            // a single 32-bit signed integer in decimal format to stdout.

            #ifdef DEBUG
            printf("You have entered the CONSOLE_WRITE_INT routine\n");
            #endif

            printf("%d", (int32_t) data);

            break;

        case CONSOLE_WRITE_UINT:
            // Console Write Unsigned Integer
            // Causes the virtual machine to print the value being stored as
            // a single 32-bit unsigned integer in lower case hexadecimal 
            // format to stdout.

            #ifdef DEBUG
            printf("You have entered the CONSOLE_WRITE_UINT routine\n");
            #endif

            printf("%x", data);

            break;

        case HALT:
            // Halt
            // Causes the virtual machine to halt the current running program,
            // then output "CPU Halt Requested" to stdout, and exit,
            // regardless the value to be stored.

            printf("CPU Halt Requested\n");
            state->running = 0;

            break;

        case DUMP_PC:
            // Dump PC
            // Causes the virtual machine to print the value of PC in 
            // lower case hexadecimal format to stdout.

            #ifdef DEBUG
            printf("You have entered the DUMP_PC routine\n");
            #endif 

            printf("%08x", state->pc);

            break;

        case DUMP_REGISTER_BANKS:
            // Dump Register Banks
            // Forces the virtual machine to perform a Register Dump.
            #ifdef DEBUG
            printf("You have entered the DUMP_REGISTER_BANKS routine\n");
            #endif

            register_dump(state);

            break;

        case DUMP_MEMORY_WORD:
            // Dump Memory Word
            // Causes the virtual machine to print the value of M[v] in
            // lower case hexadecimal format to stdout, where v is the value
            // being stored (interpreted as a 32-bit unsigned integer).

            #ifdef DEBUG
            printf("You have entered the DUMP_MEMORY_WORD routine\n");
            #endif

            printf("%08x", mem_trap_load(state, addr));

            break;
        
        case MALLOC:
            // malloc
            // Requests a chunk of memory with the size of the value being
            // stored to be allocated. The pointer of the allocated memory
            // (starting address) will be stored in R[28]. If the memory
            // cannot be allocated, R[28] = 0.

            uint32_t address = my_malloc(state, data);
            if (address == -1)
            {
                state->reg[28] = 0;
            }
            else
            {
                state->reg[28] = address;
            }

            break;
        
        case FREE:
            // free
            // Frees a chunk of memory starting at the value being stored.
            // If the address provided was not allocated, an illegal operation
            // should be raised.

            uint32_t free = my_free(state, data);
            if (free == -1)
            {
                // Raise illegal operation
                printf("Illegal Operation: 0x%08x\n", 
                                                merge_next_instruction(state));
                register_dump(state);
                state->running = 0;
            }
            else
            {
                break;
            }
        default:
            // A normal memory store
            if (addr > 2048)
            {
                state->instr_mem[addr - 2048] = data;
            }
            else
            {
                state->instr_mem[addr] = data;
            }
    }

}

uint32_t mem_trap_load(cpu_t* state, uint32_t addr)
{
    char c_input = '\0';
    int i_input = 0;

    switch (addr)
    {
        case CONSOLE_READ_CHAR:
            // Console Read Character
            // Causes the virtual machine to scan input from stdin and treat
            // the input as an ASCII-encoded character for the memory load
            // result.

            #ifdef DEBUG
            printf("You have entered the CONSOLE_READ_CHAR routine\n");
            #endif

            c_input = getchar();

            #ifdef DEBUG
            printf("You entered: %c\n", c_input);
            #endif

            return c_input;

        case CONSOLE_READ_INT:
            // Console Read Signed Integer:
            // Causes the virtual machine to scan input from stdin and parse
            // the input as a signed integer for the memory load result.

            #ifdef DEBUG
            printf("You have entered the CONSOLE_READ_INT routine\n");
            #endif

            scanf("%d", &i_input);

            #ifdef DEBUG
            printf("You entered: %d\n", i_input);
            #endif

            return i_input;
        
        default:
            // A normal memory load
            if (addr > 2048)
            {
                return state->instr_mem[addr - 2048];
            }
            else
            {
                return state->instr_mem[addr];
            }
    }
}

/*
 * The below function only runs for those which increment PC by 4
 * i.e. LOAD-type, S-type, IMM-type, R-type, LUI
 * and does not run for B-type, JAL or JALR
*/
void increment_pc(cpu_t* state)
{
    state->pc += PC_INCREMENT;
}

void execute_instr(uint32_t instr, cpu_t* state)
{
    uint32_t opcode = get_opcode(instr);

    #ifdef DEBUG
    printf("instruction: 0x%08x\n", instr);
    #endif

    switch (opcode)
    {
        /* OPCODE = 1100011 */
        /* BEQ, BNE, BLT, BLTU, BGE, BGEU */
        case OP_B_TYPE:
            B_execute_instr(instr, state);
            break;

        /* OPCODE = 0000011 */
        /* LB, LH, LW, LBU, LHU */
        case OP_LOAD_TYPE:
            LOAD_execute_instr(instr, state);
            break;
        
        /* OPCODE = 0100011 */
        /* SB, SH, SW */
        case OP_S_TYPE:
            S_execute_instr(instr, state);
            break;
        
        /* OPCODE = 0010011 */
        /* ADDI, XORI, ORI, ANDI, SLTI, SLTIU */
        case OP_IMM_TYPE:
            IMM_execute_instr(instr, state);
            break;

        /* OPCODE = 0110011 */
        /* ADD, SUB, SLL, SLT, SLTU, XOR, SRL, SRA, OR, AND */
        case OP_R_TYPE:
            R_execute_instr(instr, state);
            break;

        /* OPCODE = 0110111 */
        case OP_LUI:
            LUI_execute_instr(instr, state);
            break;            

        /* OPCODE = 1101111 */
        case OP_JAL:
            JAL_execute_instr(instr, state);
            break;            

        /* OPCODE = 1100111 */
        case OP_JALR:
            JALR_execute_instr(instr, state);
            break;

        default:            
            printf("Instruction Not Implemented: %08x\n", instr);
            register_dump(state);
    }

    if (opcode != OP_B_TYPE && opcode != OP_JAL && opcode != OP_JALR)
    {
        increment_pc(state);
    }

    // any writes to r0 are ignored
    state->reg[0] = 0;

}

void JAL_execute_instr(uint32_t instr, cpu_t* state)
{
    // Jump And Link
    // Set register rd to the address of the next instruction that
    // would otherwise be executed and then jump to the address given
    // by the sum of the pc register and the imm value.

    uint32_t rd = get_rd(instr);
    int32_t imm = J_get_imm(instr);

    #ifdef DEBUG
    printf("rd: %x\n", rd);
    printf("imm: %x\n", imm);
    #endif

    state->reg[rd] = state->pc + PC_INCREMENT;
    state->pc = state->pc + imm;

    #ifdef DEBUG
    printf("JAL\n");
    #endif
}

void LUI_execute_instr(uint32_t instr, cpu_t* state)
{
    // Load Upper Immediate
    // Set the upper half of register rd to imm.

    uint32_t rd = get_rd(instr);
    int32_t imm = U_get_imm(instr);

    #ifdef DEBUG
    printf("rd: %08x\n", rd);
    printf("imm: %08x\n", imm);
    #endif

    state->reg[rd] = imm;

    #ifdef DEBUG
    printf("LUI\n");
    #endif
}

void JALR_execute_instr(uint32_t instr, cpu_t* state)
{
    // Jump And Link Register
    // Set register rd to the address of the next instruction that 
    // would otherwise be executed and then jump to the address given
    // by the sum of the rs1 register and the imm value.
    // This instruction will explicitly set the LSB to zero regardless
    // of the value of the calculated target address, as the pc
    // register can never refer to an odd address.
    
    uint32_t rd = get_rd(instr);
    uint32_t rs1 = get_rs1(instr);
    int32_t imm = I_get_imm(instr);

    #ifdef DEBUG
    printf("rd: %08x\n", rd);
    printf("rs1: %08x\n", rs1);
    printf("imm: %08x\n", imm);
    printf("value AT rs1: %08x\n", state->reg[rs1]);
    #endif

    state->reg[rd] = state->pc + PC_INCREMENT;
    state->pc = ((state->reg[rs1] + imm) & 0xFFFFFFFE);

    #ifdef DEBUG
    printf("JALR\n");
    #endif
}

/* B-TYPE */
void B_execute_instr(uint32_t instr, cpu_t* state)
{
    int32_t imm = B_get_imm(instr);
    uint32_t funct3 = get_funct3(instr);
    uint32_t rs1 = get_rs1(instr);
    uint32_t rs2 = get_rs2(instr);

    #ifdef DEBUG
    printf("imm: %08x\n", imm);
    printf("funct3: %08x\n", funct3);
    printf("rs1: %08x\n", rs1);
    printf("rs2: %08x\n", rs2);
    printf("data at rs1: %08x\n", state->reg[rs1]);
    printf("data at rs2: %08x\n", state->reg[rs2]);
    #endif

    switch (funct3)
    {
        case BEQ:
            // Branch Equal
            // If rs1 is equal to rs2 then add imm to the pc register.

            if (state->reg[rs1] == state->reg[rs2])
            {
                state->pc += imm;
            }
            else
            {
                increment_pc(state);
            }

            #ifdef DEBUG
            printf("BEQ\n");
            #endif

            break;

        case BNE:
            // Branch Not Equal
            // If rs1 is not equal to rs2 then add imm to the pc register.

            if (state->reg[rs1] != state->reg[rs2])
            {
                state->pc = state->pc + imm;
            }
            else
            {
                increment_pc(state);
            }
            
            #ifdef DEBUG
            printf("BNE\n");
            #endif

            break;

        case BLT:
            // Branch Less Than
            // If the signed value in rs1 is less than the signed value in
            // rs2 then add imm to the pc register.

            if ((int32_t) state->reg[rs1] < (int32_t) state->reg[rs2])
            {
                state->pc = state->pc + imm;
            }
            else
            {
                increment_pc(state);
            }

            #ifdef DEBUG
            printf("BLT\n");
            #endif

            break;

        case BGE:
            // Branch Greater or Equal
            // If the signed value in rs1 is greater than or equal to the
            // signed value in rs2 then add imm to the pc register.

            if ((int32_t) state->reg[rs1] >= (int32_t) state->reg[rs2])
            {
                state->pc = state->pc + imm;
            }
            else
            {
                increment_pc(state);
            }

            #ifdef DEBUG
            printf("BGE\n");
            #endif

            break;

        case BLTU:
            // Branch Less Than Unsigned
            // If the unsigned value in rs1 is less than the unsigned value
            // in rs2 then add imm to the pc register.

            if (state->reg[rs1] < state->reg[rs2])
            {
                state->pc = state->pc + imm;
            }
            else
            {
                increment_pc(state);
            }

            #ifdef DEBUG
            printf("BLTU\n");
            #endif

            break;

        case BGEU:
            // Branch Greater or Equal Unsigned
            // If the unsigned value in rs1 is greater than or equal to the
            // unsigned value in rs2 then add imm to the pc register.

            if (state->reg[rs1] >= state->reg[rs2])
            {
                state->pc = state->pc + imm;
            }
            else
            {
                increment_pc(state);
            }

            #ifdef DEBUG
            printf("BGEU\n");
            #endif

            break;
    }
}

/* LOAD-TYPE */
void LOAD_execute_instr(uint32_t instr, cpu_t* state)
{
    uint32_t rd = get_rd(instr);
    uint32_t funct3 = get_funct3(instr);
    uint32_t rs1 = get_rs1(instr);
    int32_t imm = I_get_imm(instr);

    // store the data at the address
    uint32_t address = state->reg[rs1] + imm;
    uint32_t data = mem_trap_load(state, address);

    #ifdef DEBUG
    printf("rd: %08x\n", rd);
    printf("rs1: %08x\n", rs1);
    printf("imm: %08x\n", imm);
    printf("addr: %08x\n", address);
    printf("data: %08x\n", data);
    #endif

    
    switch (funct3)
    {
        case LB:
            // Load Byte
            // Set register rd to the value of the sign-extended byte
            // fetched from the memory address given by the sum of rs1 and imm

            // need to check if the byte at address has MSB on or off
            if (data & 0x00000080)
            {
                state->reg[rd] = 0xFFFFFF00 | data;
            }
            else
            {
                state->reg[rd] = 0x000000FF & data;
            }

            #ifdef DEBUG
            printf("LB\n");
            #endif

            break;

        case LH:
            // Load Halfword
            // Set register rd to the value of the sign-extended 16-bit
            // half-word value fetched from the memory address given by
            // the sum of rs1 and imm.

            // need to check if the halfword at address has MSB on or off
            if (data & 0x00008000)
            {
                state->reg[rd] = 0xFFFF0000 | data;
            }
            else
            {
                state->reg[rd] = 0x0000FFFF & data;
            }

            #ifdef DEBUG
            printf("LH\n");
            #endif

            break;

        case LW:
            // Load Word
            // Set register rd to the value of the sign-extended 32-bit word
            // value fetched from the memory address given by the sum of
            // rs1 and imm.

            state->reg[rd] = data;

            #ifdef DEBUG
            printf("LW\n");
            #endif

            break;

        case LBU:
            // Load Byte Unsigned
            // Set register rd to the value of the zero-extended byte fetched
            // from the memory address given by the sum of rs1 and imm.
            
            state->reg[rd] = data & 0x000000FF;

            #ifdef DEBUG
            printf("LBU\n");
            #endif

            break;

        case LHU:
            // Load Halfword Unsigned
            // Set register rd to the value of the zero-extended 16-bit
            // half-word value fetched from the memory address given by the 
            // sum of rs1 and imm.

            state->reg[rd] = data & 0x0000FFFF;

            #ifdef DEBUG
            printf("LHU\n");
            #endif

            break;
    }
}

/* S-TYPE */
void S_execute_instr(uint32_t instr, cpu_t* state)
{
    uint32_t funct3 = get_funct3(instr);
    uint32_t rs1 = get_rs1(instr);
    uint32_t rs2 = get_rs2(instr);
    int32_t imm = S_get_imm(instr);
    uint32_t address = state->reg[rs1] + imm;
    
    uint32_t data; // a variable just for storing random data inside switch

    #ifdef DEBUG
    printf("rs1: %08x\n", rs1);
    printf("rs2: %08x\n", rs2);
    printf("imm: %08x\n", imm);
    printf("address: %08x\n", address);
    printf("whats at the address before? %08x\n", state->data_mem[address]);
    #endif

    switch (funct3)
    {
        case SB:
            // Store Byte
            // Set the byte of memory at the address given by 
            // the sum of rs1 and imm to the 8 LSBs of rs2.
            
            // clear 8 LSBs at address
            data = mem_trap_load(state, address) & 0xFFFFFF00;
            
            // store 8 LSBs of rs2 in those cleared bits
            data |= (state->reg[rs2] & 0x000000FF);
            mem_trap_store(state, address, data);

            #ifdef DEBUG
            printf("SB\n");
            printf("whats at the address now? %08x\n", state->data_mem[address]);
            #endif

            break;

        case SH:
            // Store Halfword
            // Set the 16-bit halfword of memory at the address given by
            // the sum of rs1 and imm to the 16 LSBs of rs2.
            
            // clear 16 LSBs at address
            data = mem_trap_load(state, address) & 0xFFFF0000;

            // store 16 LSBs of rs2 in those cleared bits
            data |= (state->reg[rs2] & 0x0000FFFF);
            mem_trap_store(state, address, data);

            #ifdef DEBUG
            printf("SH\n");
            printf("whats at the address now? %08x\n", state->data_mem[address]);
            #endif

            break;

        case SW:
            // Store Word
            // Store the 32-bit word of memory at the address given by
            // the sum of rs1 and imm to the entire 32-bits of rs2.

            mem_trap_store(state, address, state->reg[rs2]);

            #ifdef DEBUG
            printf("SW\n");
            printf("whats at the address now? %08x\n", state->data_mem[address]);
            #endif

            break;
    }
}

/* IMM-TYPE */
void IMM_execute_instr(uint32_t instr, cpu_t* state)
{
    uint32_t rd = get_rd(instr);
    uint32_t funct3 = get_funct3(instr);
    uint32_t rs1 = get_rs1(instr);
    int32_t imm = I_get_imm(instr);

    #ifdef DEBUG
    printf("rs1: %08x\n", rs1);
    printf("rd: %08x\n", rd);
    printf("funct3: %08x\n", funct3);
    printf("imm: %08x\n", imm);
    #endif

    switch (funct3)
    {
        case ADDI:
            // Add Immediate
            // Set register rd to rs1 + imm

            state->reg[rd] = state->reg[rs1] + imm;

            #ifdef DEBUG
            printf("ADDI\n");
            #endif

            break;

        case SLTI:
            // Set Less Than Immediate
            // If the signed integer value in rs1 is less than the signed
            // integer value in imm, then set rd to 1. Otherwise, set rd to 0.

            if ((int32_t) state->reg[rs1] < imm)
            {
                state->reg[rd] = 1;
            }        
            else
            {
                state->reg[rd] = 0;
            }
            
            #ifdef DEBUG
            printf("SLTI\n");
            #endif

            break;

        case SLTIU:
            // Set Less Than Immediate Unsigned
            // If the unsigned integer value in rs1 is less than the unsigned
            // integer value in imm, then set rd to 1. Otherwise, set rd to 0.

            if (state->reg[rs1] < (uint32_t) imm)
            {
                state->reg[rd] = 1;
            }
            else
            {
                state->reg[rd] = 0;
            }
            
            #ifdef DEBUG
            printf("SLTIU\n");
            #endif

            break;
        
        case XORI:
            // Exclusive Or Immediate
            // Set register rd to the bitwise XOR of rs1 and imm.

            state->reg[rd] = state->reg[rs1] ^ imm;

            #ifdef DEBUG
            printf("XORI\n");
            #endif

            break;
        
        case ORI:
            // Or Immediate
            // Set register rd to the bitwise OR of rs1 and imm.

            state->reg[rd] = state->reg[rs1] | imm;

            #ifdef DEBUG
            printf("ORI\n");
            #endif

            break;

        case ANDI:
            // And Immediate
            // Set register rd to the bitwise AND of rs1 and imm.

            state->reg[rd] = state->reg[rs1] & imm;

            #ifdef DEBUG
            printf("ANDI\n");
            #endif

            break;
    }
}

/* R-TYPE */
void R_execute_instr(uint32_t instr, cpu_t* state)
{
    uint32_t rd = get_rd(instr);
    uint32_t funct3 = get_funct3(instr);
    uint32_t rs1 = get_rs1(instr);
    uint32_t rs2 = get_rs2(instr);
    uint32_t funct7 = get_funct7(instr);
    uint32_t shift_amt;

    switch (funct3)
    {
        case ADD_SUB:

            if (funct7 == FUNCT_7_OFF)
            {
                // Add
                // Set register rd to rs1 + rs2.

                state->reg[rd] = state->reg[rs1] + state->reg[rs2];

                #ifdef DEBUG
                printf("ADD\n");
                #endif

                break;
            }
            else if (funct7 == FUNCT_7_ON)
            {
                // Subtract
                // Set register rd to rs1 - rs2.

                state->reg[rd] = state->reg[rs1] - state->reg[rs2];

                #ifdef DEBUG
                printf("SUB\n"); 
                #endif

                break;
            }
            else
            {
                printf("INVALID!!!\n");
            }

            break;

        case SLL:
            // Shift Left Logical
            // Shift rs1 left by the number of bits specified in the least
            // significant 5 bits of rs2 and store the result in rd.
            
            shift_amt = (state->reg[rs2] & 0x0000001F);
            state->reg[rd] = state->reg[rs1] << shift_amt;

            #ifdef DEBUG
            printf("SLL\n");
            #endif

            break;
        
        case SLT:
            // Set Less Than
            // If the signed int value in rs1 is less than the signed int 
            // value in rs2, set rd to 1. Otherwise set rd to 0.
            
            if ((int32_t) state->reg[rs1] < (int32_t) state->reg[rs2])
            {
                state->reg[rd] = 1;
            }
            else
            {
                state->reg[rd] = 0;
            }

            #ifdef DEBUG
            printf("SLT\n");
            #endif

            break;
        
        case SLTU:
            // Set Less Than Unsigned
            // If the unsigned int value in rs1 is less than the unsigned int 
            // value in rs2, set rd to 1. Otherwise set rd to 0.
            
            if (state->reg[rs1] < state->reg[rs2])
            {
                state->reg[rd] = 1;
            }
            else
            {
                state->reg[rd] = 0;
            }

            #ifdef DEBUG
            printf("SLTU\n");
            #endif

            break;
        
        case XOR:
            // Exclusive Or
            // Set register rd to the bitwise XOR of rs1 and rs2.

            state->reg[rd] = state->reg[rs1] ^ state->reg[rs2];

            #ifdef DEBUG
            printf("XOR\n");
            #endif

            break;
        
        case SRL_SRA:
            if (funct7 == FUNCT_7_OFF)
            {
                // Shift Right Logical
                // Logic-shift rs1 right by the number of bits given in the
                // least-significant 5 bits of the rs2 register and store
                // the result in rd.

                shift_amt = (state->reg[rs2] & 0x0000001F);
                state->reg[rd] = state->reg[rs1] >> shift_amt;

                #ifdef DEBUG
                printf("SRL\n");
                #endif

                break;                
            }
            else if (funct7 == FUNCT_7_ON)
            {
                // Shift Right Arithmetic
                // Arithmetic-shift rs1 right by the number of bits given in 
                // the least-significant 5 bits of the rs2 register and store
                // the result in rd. 

                shift_amt = state->reg[rs2] & 0x1F;
                if (MSB_set(rs1))
                {
                    state->reg[rd] = state->reg[rs1];
                    while (shift_amt > 0)
                    {
                        state->reg[rd] = (state->reg[rd] >> 1) | 0x80000000;
                        shift_amt--;
                    }                    
                }
                else
                {
                    state->reg[rd] = state->reg[rs1] >> shift_amt;
                }

                #ifdef DEBUG
                printf("SRA\n");
                #endif

                break;
            }
            else
            {
                printf("INVALID!!!\n");
            }

            break;
        
        case OR:
            // Or
            // Set register rd to the bitwise OR of rs1 and rs2.

            state->reg[rd] = state->reg[rs1] & state->reg[rs2];

            #ifdef DEBUG
            printf("OR");
            #endif

            break;
        
        case AND:
            // And
            // Set register rd to the bitwise AND of rs1 and rs2.

            state->reg[rd] = state->reg[rs1] & state->reg[rs2];

            #ifdef DEBUG
            printf("AND");
            #endif

            break;
    }
}