import glob
import csv

print('merge different UMLS dics')

def create_big_dic(dic_path, delim):
	dics = glob.glob(dic_path + '/*')
	big_dic = ''
	for dic in dics:
		name = dic.replace(dic_path + '/', '').replace('.txt', '').replace('.dict', '').replace('2019AB-', '').replace('-GER', '')
		with open(dic) as tsvfile:
			reader = csv.reader(tsvfile, delimiter=delim)
			for row in reader:
				#print(row[0])
				big_dic += row[0] + '\t' + name + '\n'
	return big_dic

path = '/the/name/of/the/path/with/dictionary/files'

dic_path_umls = path + '/UMLS-semantic-group'
big_dic_umls = create_big_dic(dic_path_umls, '|')

dic_path_gene = path + '/gene'
big_dic_gene = create_big_dic(dic_path_gene, '\t')

big_dic_file = open('bic_dic.txt', 'w')
big_dic_file.write(big_dic_umls)
big_dic_file.write(big_dic_redlist)
big_dic_file.write(big_dic_gene)
big_dic_file.close()
