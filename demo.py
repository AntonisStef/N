import bcg
# use main table
bc=bcg.BolometryTable('data/bc_dr3_feh_all.txt')
point=[2550.,5.,-0.5,-0.2]
offset = 0.12
# without offset
print(bc.computeBc(point))
# with offset
print(bc.computeBc(point,offset))



