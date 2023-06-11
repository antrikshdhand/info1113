	.section	__TEXT,__text,regular,pure_instructions
	.build_version macos, 12, 0	sdk_version 12, 3
	.globl	_prints                         ; -- Begin function prints
	.p2align	2
_prints:                                ; @prints
	.cfi_startproc
; %bb.0:
	sub	sp, sp, #16
	.cfi_def_cfa_offset 16
	str	x0, [sp, #8]
	b	LBB0_1
LBB0_1:                                 ; =>This Inner Loop Header: Depth=1
	ldr	x8, [sp, #8]
	ldrb	w8, [x8]
	cbz	w8, LBB0_3
	b	LBB0_2
LBB0_2:                                 ;   in Loop: Header=BB0_1 Depth=1
	ldr	x8, [sp, #8]
	add	x9, x8, #1
	str	x9, [sp, #8]
	ldrb	w8, [x8]
	mov	x9, #2048
	strb	w8, [x9]
	b	LBB0_1
LBB0_3:
	add	sp, sp, #16
	ret
	.cfi_endproc
                                        ; -- End function
	.globl	_main                           ; -- Begin function main
	.p2align	2
_main:                                  ; @main
	.cfi_startproc
; %bb.0:
	sub	sp, sp, #64
	stp	x29, x30, [sp, #48]             ; 16-byte Folded Spill
	add	x29, sp, #48
	.cfi_def_cfa w29, 16
	.cfi_offset w30, -8
	.cfi_offset w29, -16
	mov	w8, #0
	stur	w8, [x29, #-16]                 ; 4-byte Folded Spill
	stur	wzr, [x29, #-4]
	bl	_scan_char
	stur	w0, [x29, #-8]
	bl	_scan_char
	stur	w0, [x29, #-12]
	adrp	x0, l_.str@PAGE
	add	x0, x0, l_.str@PAGEOFF
	str	x0, [sp, #8]                    ; 8-byte Folded Spill
	bl	_prints
	ldur	w0, [x29, #-8]
	ldur	w1, [x29, #-12]
	bl	_lshift
	mov	x8, #2056
	str	x8, [sp, #16]                   ; 8-byte Folded Spill
	str	w0, [x8]
	adrp	x0, l_.str.1@PAGE
	add	x0, x0, l_.str.1@PAGEOFF
	str	x0, [sp, #24]                   ; 8-byte Folded Spill
	bl	_prints
	ldr	x0, [sp, #8]                    ; 8-byte Folded Reload
	bl	_prints
	ldur	w0, [x29, #-8]
	ldur	w1, [x29, #-12]
	bl	_rshift
	ldr	x9, [sp, #16]                   ; 8-byte Folded Reload
	mov	x8, x0
	ldr	x0, [sp, #24]                   ; 8-byte Folded Reload
	str	w8, [x9]
	bl	_prints
	ldur	w0, [x29, #-16]                 ; 4-byte Folded Reload
	ldp	x29, x30, [sp, #48]             ; 16-byte Folded Reload
	add	sp, sp, #64
	ret
	.cfi_endproc
                                        ; -- End function
	.section	__TEXT,__const
	.globl	_ConsoleWriteHex                ; @ConsoleWriteHex
	.p2align	3
_ConsoleWriteHex:
	.quad	2056

	.section	__TEXT,__cstring,cstring_literals
l_.str:                                 ; @.str
	.asciz	"0x"

l_.str.1:                               ; @.str.1
	.asciz	"\n"

.subsections_via_symbols
