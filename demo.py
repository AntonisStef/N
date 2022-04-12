import bcg
# use main table
bc=bcg.BolometryTable('data/bc_dr3_feh_all.txt')
point=[5772.,4.43,0,0]
offset = 0.
# without offset
print(bc.computeBc(point))
# with offset
print(bc.computeBc(point,offset))



