import os
import regex as re
import pandas as pd
import shutil
from bratutils import agreement as a
import sys

# Configuration

ignore_classes = [
    ('agreement', 'Genes')
]

classes = {
    'ANAT' : ['Anatomical_Structure'],
    'CHEM' : ['Chemicals_Drugs'],
    'DEVI' : ['Devices'],
    'DISO' : ['Disorders'],
    'LIVB' : ['Living_Beings'],
    'PHYS' : ['Physiology'],
    'PROC' : ['Procedures'],
    'TNM' : ['TNM'],
    'ALL' : [
         'Anatomical_Structure',
         'Chemicals_Drugs',
         'Disorders',
         'Procedures',
         'Living_Beings',
         'Devices',
         'Physiology',
          #'Genes',
         'TNM'
    ],
    'GENE' : ['Genes'],
}

def keep_line(line, keep_classes, file):
    if line.startswith('A') or not line.strip():
        return ''
    for k in keep_classes:
        try:
            if line.split('\t')[1].startswith(k):
                return line
        except Exception as e:
            print(file)
            print(line)
            raise e
    return ''

def make_copy(folder, target_folder, keep_classes=[]):
    ignored_files = []
    shutil.rmtree(target_folder, ignore_errors=True)
    os.makedirs(target_folder)
    for root, subdirs, files in os.walk(folder):
        for f in files:
            if f.endswith('ann'):
                keep_classes_clean = keep_classes.copy()
                for k, v in ignore_classes:
                    if ((k in f) or (k in root)) and v in keep_classes_clean:
                        keep_classes_clean.remove(v)
                        ignored_files.append((k, v, f))
                file_name = re.sub(r'([\\/.]|Gold|Raw)', '', root) + '_' + f
                with open(root + '/' + f, 'r') as in_file:
                    with open(target_folder + '/' + file_name, 'w') as out_file:
                        out = ''
                        for i, line in enumerate(in_file.readlines()):
                            kl = keep_line(line, keep_classes_clean, f)
                            out += kl#kl.replace('^T\d+', 'T%d' % i)
                        out_file.write(out)
    return ignored_files

def run_evaluation(gold_folder, raw_folder):
    root_folder = '.'

    all_stats = []
    ignored = {}

    for key, keep_classes in classes.items():
        gold_target = os.path.join(root_folder, 'temp', 'GOLD-' + key)
        raw_target = os.path.join(root_folder, 'temp', 'AUTO-' + key)
        ignored_gold = make_copy(gold_folder, gold_target, keep_classes=keep_classes)
        ignored_raw = make_copy(raw_folder, raw_target, keep_classes=keep_classes)
                
        ignored[key] = (ignored_gold, ignored_raw)
        
        stats = []
        
        n = 0
        
        for f in os.listdir(raw_target):        
            d = a.Document(raw_target + '/' + f)
            dg = a.Document(gold_target + '/' + f)
            
            statistics = dg.compare_to_gold(d)
            statistics.update_table(statistics.STRICT_COMPARISON)
            
            tp = statistics.cor
            fn = statistics.mis
            fp = statistics.spu
            
            print((tp, fn, fp))
            
            stats.append((tp, fn, fp))
        
        tp = sum([s[0] for s in stats])
        fn = sum([s[1] for s in stats])
        fp = sum([s[2] for s in stats])
        
        pr = tp / (tp + fp) if (tp + fp) > 0 else 0
        rec = tp / (tp + fn) if (tp + fn) > 0 else 0
            
        all_stats.append({'KEY' : key, 'Precision' : pr, 'Recall' : rec})
    df = pd.DataFrame(all_stats)
    df.style.format({'Precision' : "{:.3}", 'Recall' : "{:.3}"})
    print(df)

if __name__ == "__main__":
    print('Gold standard: ' + sys.argv[1])
    print('Predictions: ' + sys.argv[2])
    run_evaluation(sys.argv[1], sys.argv[2])