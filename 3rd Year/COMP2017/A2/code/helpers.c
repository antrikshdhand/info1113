#include <stdio.h> // ONLY FOR PRINT DEBUGGING
#include <stdint.h>

#include "helpers.h"

uint32_t merge_next_instruction(cpu_t* state)
{
    uint32_t first_byte = state->instr_mem[state->pc];
    uint32_t second_byte = state->instr_mem[state->pc + 0x1] << 8;
    uint32_t third_byte = state->instr_mem[state->pc + 0x2] << 16;
    uint32_t fourth_byte = state->instr_mem[state->pc + 0x3] << 24;

    return fourth_byte | third_byte | second_byte | first_byte;
}

void register_dump_wide(cpu_t* state)
{
    printf("PC = %d;\n", state->pc);
    for (int i = 0; i < 7; i++)
    {
        printf("R[%d] = 0x%x, R[%d] = 0x%x, R[%d] = 0x%x, R[%d] = 0x%x, R[%d] = 0x[%d]\n", i, state->reg[i], i+7, state->reg[i+7], i+14, state->reg[i+14], i+21, state->reg[i+21], i+25, state->reg[i+25]);
    }
}

void register_dump(cpu_t* state)
{
    printf("PC = 0x%08x;\n", state->pc);
    for (int i = 0; i < 31; i++)
    {
        printf("R[%d] = 0x%08x;\n", i, state->reg[i]);
    }
}

/* HELPER FUNCTIONS FOR ALL INSTRUCTION TYPES */
int MSB_set(const uint32_t instr)
{
    return instr & 0x80000000;
}

uint32_t get_opcode(uint32_t instr)
{
    return instr & 0x0000007F;
}

uint32_t get_rd(uint32_t instr)
{
    return (instr & 0x00000F80) >> 7;
}

uint32_t get_funct3(uint32_t instr)
{
    return (instr & 0x00007000) >> 12;
}

uint32_t get_rs1(uint32_t instr)
{
    return (instr & 0x000F8000) >> 15;
}

uint32_t get_rs2(uint32_t instr)
{
    return (instr & 0x01F00000) >> 20;
}

uint32_t get_funct7(uint32_t instr)
{
    return (instr & 0xFE000000) >> 25;
}


/* GET IMMEDIATE VALUES FOR EACH INSTRUCTION TYPE */
int32_t U_get_imm(uint32_t instr)
{
    // [31:12]
    return (instr & 0xFFFFF000);
}

int32_t J_get_imm(uint32_t instr)
{
    uint32_t imm20 = (instr >> 11) & 0x00100000;
    uint32_t imm10_1 = (instr & 0x7FE00000) >> 20;
    uint32_t imm11 = (instr & 0x00100000) >> 9;
    uint32_t imm19_12 = (instr & 0x000FF000);

    int32_t base = imm20 | imm19_12 | imm11 | imm10_1;
    
    if (MSB_set(instr))
    {
        return 0xFFE00000 | base;
    }
    else
    {
        return base;
    }
}

int32_t I_get_imm(uint32_t instr)
{
    if (MSB_set(instr))
    {
        return 0xFFFFF000 | instr >> 20;;
    }
    else
    {
        return instr >> 20;;
    }
}

int32_t B_get_imm(uint32_t instr)
{
    uint32_t imm12 = (instr & 0x80000000) >> 19;
    uint32_t imm11 = (instr & 0x00000080) << 4;
    uint32_t imm10_5 = (instr & 0x7E000000) >> 20;
    uint32_t imm4_1 = (instr & 0x00000F00) >> 7;
    
    uint32_t base = imm12 | imm11 | imm10_5 | imm4_1;

    if (MSB_set(instr))
    {
        return 0xFFFFE000 | base;
    }
    else
    {
        return base;
    }
}

int32_t S_get_imm(uint32_t instr)
{
    uint32_t imm11_5 = (instr & 0xFE000000) >> 20;
    uint32_t imm4_0 = (instr & 0x00000F80) >> 7;

    uint32_t base = imm11_5 | imm4_0;

    if (MSB_set(instr))
    {
        return 0xFFFFF000 | base;
    }
    else
    {
        return base;
    }
}