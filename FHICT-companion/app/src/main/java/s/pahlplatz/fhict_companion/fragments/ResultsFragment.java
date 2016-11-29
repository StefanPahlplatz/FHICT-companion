package s.pahlplatz.fhict_companion.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import s.pahlplatz.fhict_companion.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment implements TokenFragment.OnFragmentInteractionListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TokenFragment.OnFragmentInteractionListener mListener;

    public ResultsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(String param1, String param2)
    {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        onFragmentInteraction("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IlJ6WjZ0b1RSMXh5WXNXNU5YWHl6eXUxSEpLMCIsImtpZCI6IlJ6WjZ0b1RSMXh5WXNXNU5YWHl6eXUxSEpLMCJ9.eyJpc3MiOiJodHRwczovL2lkZW50aXR5LmZoaWN0Lm5sIiwiYXVkIjoiaHR0cHM6Ly9pZGVudGl0eS5maGljdC5ubC9yZXNvdXJjZXMiLCJleHAiOjE0ODA0NDk0MzEsIm5iZiI6MTQ4MDQ0NTgzMSwiY2xpZW50X2lkIjoiYXBpLWNsaWVudCIsInVybjpubC5maGljdDp0cnVzdGVkX2NsaWVudCI6InRydWUiLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwiZW1haWwiLCJmaGljdCIsImZoaWN0X3BlcnNvbmFsIiwiZmhpY3RfbG9jYXRpb24iXSwic3ViIjoiNzdhNzhjNDgtZDQ1Zi00ODc0LWE3ZmYtODgwMmRmOWU0NzE2IiwiYXV0aF90aW1lIjoxNDgwNDQ1ODIxLCJpZHAiOiJmaGljdC1zc28iLCJyb2xlIjpbInVzZXIiLCJzdHVkZW50Il0sInVwbiI6IkkzNjQyMzdAZmhpY3QubmwiLCJuYW1lIjoiUGFobHBsYXR6LFN0ZWZhbiBTLiIsImVtYWlsIjoicy5wYWhscGxhdHpAc3R1ZGVudC5mb250eXMubmwiLCJ1cm46bmwuZmhpY3Q6c2NoZWR1bGUiOiJjbGFzc3xFaTFEIC8gbmV3RVMiLCJmb250eXNfdXBuIjoiMzY0MjM3QHN0dWRlbnQuZm9udHlzLm5sIiwiYW1yIjpbImV4dGVybmFsIl19.tXCI6zB1_3B2-rKNRs-aADBglk6jaGx-zNukreK_JlZYD97ZaZ8qL2S8uTKn-VZG06BQ7f7x9_Xfs6coEtE-xkN-wDVJgYrwgy6Sr5j8EiTA4v3OO6bO_h_5XzopLpYDGBN2w74cXyZonzci_GYkblYvp5Ka2NM7dZVoWhxplSfwFIkNCkAs9rkAJCsOm5auennLALn5CE8_qz_l6CDo6x55SXjv41zg8ZJqbpjutmoij4tYkxz_DyOex6c6v0l1Tf8FVAsvCghaICj3Nlu2F-TiVnqX2HOM5K1axpPD3tVuFHnpbEm8O_kz1PGzhKFaiWrfi6WXpBz8Ji7VmfzvFA");
    }

    @Override
    public void onFragmentInteraction(String token)
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_results, container, false);

        // Do stuff

        return view;
    }
}
