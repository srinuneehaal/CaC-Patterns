from pathlib import Path
path = Path('spec/Accounts/accounts-plan.mmd')
for idx, line in enumerate(path.read_text().splitlines(), 1):
    print(f'{idx}: {line}')
