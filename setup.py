from setuptools import setup, find_packages
from gdr3bcg import __VERSION__

def read(file):
    with open(file) as f:
        return f.read()

setup(name = "gdr3bcg",
    version = __VERSION__,
    description = "A tool for the user to calculate bolometric corrections in the Gaia (E)DR3 G-band.",
    long_description = read('README.md'),
    author = "Christophe Ordenovic",
    author_email = "christophe.ordenovic@oca.eu",
    url = "https://www.cosmos.esa.int/web/gaia/dr3-bolometric-correction-tool",
    packages = find_packages(),
    package_data = {'gdr3bcg':['data/*']},
    include_package_data = True,
    classifiers=[
      'Intended Audience :: Science/Research',
      'Operating System :: OS Independent',
      'Programming Language :: Python :: 3',
      'License :: OSI Approved :: MIT License',
      'Topic :: Scientific/Engineering :: Astronomy'
      ],
    zip_safe=False,
    python_requires=">=3.6",
    install_requires=['numpy>=1.17.0', 'pandas>=1.0.0']
)
