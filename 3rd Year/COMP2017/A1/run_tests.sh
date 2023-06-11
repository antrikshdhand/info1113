for file in tests/*.in
do
    out_file=$(basename "${file%.*}.out")

    echo "Diffing $file and $out_file"
    ./gitm < $file | diff - tests/$out_file
done
