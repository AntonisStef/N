import bcg
# use main table
bc=bcg.BolometryTable('data/bc_dr3_feh_all.txt')
point=[5772.,4.43,0,0]
offset = 0.02
# without offset
print(' ')
print('The BC_G for the Sun (run command "bc.computeBc([5772,4.43,0,0])")')
print(' = ',bc.computeBc(point), 'mag')
# with offset
print('')
print('If we want to modify the zeropoint to BC_G,Sun = +0.10 mag (i.e. MG_G,Sun = 4.64 mag)' )
print('    instead of the default +0.08 mag (M_G,Sun = 4.66)')
print('then we run "bc.computeBc([5772,4,43,0,0],+0.02)" and obtain')
print('       BC_G = ',bc.computeBc(point,offset),' mag')
print('  ')




