#include <stdio.h>
#include <stdint.h>

#include "heaps.h"
#include "helpers.h"
#include "instructions.h"

int main(int argc, char* argv[])
{
    cpu_t state = {
        .instr_mem = {0},
        .data_mem = {0},
        .reg = {0},
        .pc = 0b0,
        .heap = 0,
        .running = 1
    };

    if (argc != 2)
    {
        printf("Invalid number of CLI arguments.\n");
        return 1;
    }

    FILE* binary_f = fopen(argv[1], "rb");

    if (binary_f == NULL)
    {
        printf("Error: file not found.\n");
        return 1;
    }

    fread(&state, 1, 2048, binary_f);

    intialise_heap_banks(&state);

    while (state.running)
    {   
        uint32_t instr = merge_next_instruction(&state);
        execute_instr(instr, &state);
    }

    list_free(&state.heap);

}

