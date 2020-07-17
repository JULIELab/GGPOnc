import glob
import csv

# before usage:
# * create UMLS dictionaries with JuFiT
# * create Gene dictionary with JULIELab/gene-name-mapping: https://zenodo.org/record/3874895#.XxG0Zh0aRhE
# * adapt path names of your dictionaries

def create_global_dict(dic_path, delim):
	dics = glob.glob(dic_path + '/*')
	global_dict = ''
	for dic in dics:
		name = dic.replace(dic_path + '/', '').replace('.txt', '').replace('.dict', '').replace('2019AB-', '').replace('-GER', '')
		with open(dic) as tsvfile:
			reader = csv.reader(tsvfile, delimiter=delim)
			for row in reader:
				#print(row[0])
				global_dict += row[0] + '\t' + name + '\n'
	return global_dict

# todo apapt before usage
path = '/the/name/of/the/path/with/dictionary/files'

dic_path_umls = path + '/UMLS-semantic-group'
global_dict_umls = create_global_dict(dic_path_umls, '|')

dic_path_gene = path + '/gene'
global_dict_gene = create_global_dict(dic_path_gene, '\t')

global_dict_file = open('global_dictionary.txt.txt', 'w')
global_dict_file.write(global_dict_umls)
global_dict_file.write(global_dict_gene)
global_dict_file.close()